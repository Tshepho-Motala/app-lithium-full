package lithium.service.raf.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.DomainClient;
import lithium.service.raf.data.enums.AutoConvertPlayer;
import lithium.service.raf.enums.RAFConversionType;
import lithium.service.raf.enums.ReferralConversionStatus;
import lithium.service.xp.client.objects.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.entities.Domain;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.repositories.ReferralRepository;
import lithium.service.raf.data.specifications.ReferralSpecification;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.util.Optional;

import static lithium.service.accounting.objects.Period.GRANULARITY_TOTAL;
import static lithium.service.raf.enums.ReferralConversionStatus.ALREADY_REFERRED;
import static lithium.service.raf.enums.ReferralConversionStatus.PASSED_CONVERSION_CRITERIA;
import static lithium.service.raf.enums.ReferralConversionStatus.SUCCESS_REFERRAL;
import static lithium.service.raf.enums.ReferralConversionStatus.SUCCESS_REFERRAL_AND_CONVERTED;

@Slf4j
@Service
public class ReferralService {
	@Autowired BonusService bonusService;
	@Autowired DomainService domainService;
	@Autowired ExternalUserService externalUserService;
	@Autowired ReferrerService referrerService;
	@Autowired ConfigurationService configurationService;
	@Autowired ReferralRepository repository;
	@Autowired NotificationStream notificationStream;

	private static final String DEPOSIT = "CASHIER_DEPOSIT";

	@Autowired
	private LithiumServiceClientFactory services;

	private static final int DEPOSIT_COUNT = 1;
	@Autowired
	private ExternalXPService externalXPService;

	public Referral add(String referrerGuid, String playerGuid) throws Exception {
		String domainAndPlayer[] = referrerGuid.split("/");
		if (domainAndPlayer.length != 2 || domainAndPlayer[0].isEmpty() || domainAndPlayer[1].isEmpty())
			throw new Exception("referrerGuid is not valid");
		Domain domain = domainService.findOrCreate(domainAndPlayer[0]);
		Referrer referrer = referrerService.findOrCreate(referrerGuid);

		// check if the player already has a referral?
		Referral referral = repository.findByPlayerGuid(playerGuid);
		if(referral!=null){
			return referral;
		}
		//
		referral = Referral.builder()
		.referrer(referrer)
		.playerGuid(playerGuid)
		.domain(domain)
		.build();
		referral = repository.save(referral);
		notifyReferrer(domain, referrer);
		//TODO: Promo integration
//		missionService.streamStat(referrerGuid, Type.TYPE_RAF, Action.ACTION_REFERERAL, null, null);
		return referral;
	}

	public ReferralConversionStatus addReferralAfterSignUp(String referrerGuid, String playerGuid) throws Exception {
		Referral referral = repository.findByPlayerGuid(playerGuid);
		if(referral!=null){
			return ALREADY_REFERRED;
		}
		String domainAndPlayer[] = referrerGuid.split("/");
		String domainName=domainAndPlayer[0];
			Domain domain = domainService.findOrCreate(domainName);
			Referrer referrer = referrerService.findOrCreate(referrerGuid);
				referral = Referral.builder()
						.referrer(referrer)
						.playerGuid(playerGuid)
						.domain(domain)
						.build();
				return convertPlayer(referral, domain);
	}

	private ReferralConversionStatus convertPlayer(Referral referral,Domain domain) throws Exception {
		Level playerLevel = externalXPService.getUserLevel(referral.getPlayerGuid(), domain.getName());
		Configuration configuration = configurationService.findOrCreate(domain.getName());
		AutoConvertPlayer autoConvertPlayer = configuration.getAutoConvertPlayer();
		Integer conversionXpLevel = configuration.getConversionXpLevel();
		RAFConversionType conversionType = configuration.getConversionType();

		if (autoConvertPlayer == null || playerLevel == null || conversionXpLevel == null || conversionType == null) {
			repository.save(referral);
			notifyReferrer(domain, referral.getReferrer());
			return SUCCESS_REFERRAL;
		}
		switch (autoConvertPlayer){
			case ENABLED: {
				switch (conversionType){
					case XP_LEVEL:{
						if (playerLevel.getNumber().intValue() >= conversionXpLevel){
							processConversion(referral);
							notifyReferrer(domain, referral.getReferrer());
							return SUCCESS_REFERRAL_AND_CONVERTED;
						} else {
							repository.save(referral);
							notifyReferrer(domain, referral.getReferrer());
							return SUCCESS_REFERRAL;
						}
					}
					case DEPOSIT:{
						SummaryAccountTransactionType deposits = checkDeposits(domain.getName(), referral.getPlayerGuid(), GRANULARITY_TOTAL);
						Long depositCount=deposits.getTranCount();
						if (depositCount != null && depositCount >= DEPOSIT_COUNT) {
							processConversion(referral);
							notifyReferrer(domain, referral.getReferrer());
							return SUCCESS_REFERRAL_AND_CONVERTED;
						} else {
							notifyReferrer(domain, referral.getReferrer());
							repository.save(referral);
							return SUCCESS_REFERRAL;
						}
					}
					default:{
						//block referral
						return PASSED_CONVERSION_CRITERIA;
					}
				}
			}
			case DISABLED: {
				switch (conversionType){
					case XP_LEVEL:{
						if (playerLevel.getNumber().intValue() < conversionXpLevel) {
							notifyReferrer(domain, referral.getReferrer());
							repository.save(referral);
							return SUCCESS_REFERRAL;
						} else {
							//block add referral
							return PASSED_CONVERSION_CRITERIA;
						}
					}
					case DEPOSIT: {
						//  check if player has made deposit yet
						SummaryAccountTransactionType deposits = checkDeposits(domain.getName(), referral.getPlayerGuid(), GRANULARITY_TOTAL);
						Long depositCount=deposits.getTranCount();
						if (depositCount != null && depositCount < DEPOSIT_COUNT) {
							// add referral no conversion
							repository.save(referral);
							notifyReferrer(domain, referral.getReferrer());
							return SUCCESS_REFERRAL;
						} else {
							// block add referral
							return PASSED_CONVERSION_CRITERIA;
						}
					}
					default:{
						//  block referral
						return PASSED_CONVERSION_CRITERIA;
					}
				}
			}
			default:{
				// add referral no conversion
				repository.save(referral);
				notifyReferrer(domain, referral.getReferrer());
				return SUCCESS_REFERRAL;
			}
		}
	}

	private void notifyReferrer(Domain domain, Referrer referrer) {
		Configuration c = configurationService.findOrCreate(domain.getName());
		log.info("Queueing : "+c.getReferralNotification()+" for "+referrer.getPlayerGuid());
		notificationStream.process(
			UserNotification.builder()
			.userGuid(referrer.getPlayerGuid())
			.notificationName(c.getReferralNotification())
			.build()
		);
	}

	public Page<Referral> findByReferrer(String playerGuid, DataTableRequest request) {
		Page<Referral> page = repository.findAll(ReferralSpecification.referrerIs(playerGuid), request.getPageRequest());
		log.info("page"+page);
		return page;
	}
	
	public Referral findByPlayerGuid(String domainName, String userName) {
		String playerGuid = domainName + "/" + userName;
		Referral referral = repository.findByPlayerGuid(playerGuid);
		if (referral != null) {
			try {
				referral.setFullReferrer(externalUserService.getExternalUser(referral.getReferrer().getPlayerGuid()));
			} catch (LithiumServiceClientFactoryException e) {
			}
		}
		return referral;
	}
	
//	public Referral markConverted(String domainName, String userName) {
//		String playerGuid = domainName + "/" + userName;
//		Referral referral = repository.findByPlayerGuid(playerGuid);
//		referral.setConverted(true);
//		return repository.save(referral);
//	}
	
	public Page<Referral> findByDomain(boolean converted, String domainName, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		Specification<Referral> spec = Specification.where(ReferralSpecification.domain(domainName));
		spec = spec.and(ReferralSpecification.convertedIs(converted));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Referral> s = Specification.where(ReferralSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Referral> result = repository.findAll(spec, pageable);
		for (Referral r: result.getContent()) {
			try {
				r.setFullReferrer(externalUserService.getExternalUser(r.getReferrer().getPlayerGuid()));
				r.setFullUser(externalUserService.getExternalUser(r.getPlayerGuid()));
			} catch (LithiumServiceClientFactoryException e) {
			}
		}
		return result;
	}


	
	public void processConversion(Referral referral) {
		referral.setConverted(true);
		referral = repository.save(referral);
		//TODO: Promo integration
//		missionService.streamStat(referral.getReferrer().getPlayerGuid(), Type.TYPE_RAF, Action.ACTION_CONVERSATION, null, null);
		bonusService.triggerReferralBonus(
			referral.getDomain().getName(),
			referral.getReferrer().getPlayerGuid(),
			referral.getPlayerGuid());
	}

	private AccountingSummaryTransactionTypeClient getAccountingSummaryTransactionTypeService() {
		AccountingSummaryTransactionTypeClient cl = null;
		try {
			cl = services.target(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return cl;
	}

	public SummaryAccountTransactionType checkDeposits(String domainName, String ownerGuid, int granularity) throws Exception {
		Response<SummaryAccountTransactionType> stt = getAccountingSummaryTransactionTypeService().find(
				DEPOSIT,
				domainName,
				URLEncoder.encode(ownerGuid, "UTF-8"),
				granularity,
				getDomainClient().get().findByName(domainName).getData().getCurrency()
		);
		if (stt.isSuccessful()) {
			return stt.getData();
		}
		return null;
	}

	public Optional<DomainClient> getDomainClient() {
		return getClient(DomainClient.class, "service-domain");
	}

	public <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);

	}
}
