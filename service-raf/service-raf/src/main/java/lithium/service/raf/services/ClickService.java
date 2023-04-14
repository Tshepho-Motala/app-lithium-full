package lithium.service.raf.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.raf.data.entities.Click;
import lithium.service.raf.data.entities.Domain;
import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.repositories.ClickRepository;
import lithium.service.raf.data.specifications.ClickSpecification;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.user.client.UserApiClient;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ClickService {
	@Autowired DomainService domainService;
	@Autowired ExternalUserService externalUserService;
	@Autowired ReferrerService referrerService;
	@Autowired ClickRepository repository;
	@Autowired StatsStream statsStream;
	@Autowired LithiumServiceClientFactory services;
	
	public Click click(String playerGuid, String ip, String userAgent) throws Exception {
		playerGuid = lookupReferrerCode(playerGuid);
		String domainAndPlayer[] = playerGuid.split("/");
		if (domainAndPlayer.length != 2 || domainAndPlayer[0].isEmpty() || domainAndPlayer[1].isEmpty())
			throw new Exception("playerGuid is not valid");
		Domain domain = domainService.findOrCreate(domainAndPlayer[0]);
		Referrer referrer = referrerService.findOrCreate(playerGuid);
		Click click = Click.builder()
		.referrer(referrer)
		.domain(domain)
		.ip(ip)
		.userAgent(userAgent)
		.build();
		click = repository.save(click);
		statsStream(playerGuid, ip, userAgent);
		return click;
	}

	private void statsStream(String playerGuid, String ip, String userAgent) {
		QueueStatEntry queueStatEntry = QueueStatEntry.builder()
		.type(lithium.service.stats.client.enums.Type.RAF.type())
		.event(Event.CLICK.event())
		.entry(
			StatEntry.builder()
			.name(
				"stats." +
				lithium.service.stats.client.enums.Type.RAF.type() + "." +
				playerGuid.replaceAll("/", ".") + "." +
				Event.CLICK.event()
			)
			.domain(playerGuid.split("/")[0])
			.ownerGuid(playerGuid)
			.ipAddress(ip)
			.userAgent(userAgent)
			.build()
		)
		.build();
		statsStream.register(queueStatEntry);
	}
	
	public Page<Click> findByDomain(String domainName, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		Specification<Click> spec = Specification.where(ClickSpecification.domain(domainName));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Click> s = Specification.where(ClickSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Click> result = repository.findAll(spec, pageable);
		for (Click r: result.getContent()) {
			try {
				r.setFullReferrer(externalUserService.getExternalUser(r.getReferrer().getPlayerGuid()));
			} catch (LithiumServiceClientFactoryException e) {
			}
		}
		return result;
	}
	
	/**
	 * Lookup the shortGuid field in the user API table to resolve the referrer user guid.
	 * If the lookup fails, we assume the code is already the user guid.
	 * @param referrerGuidOrReferrerCode
	 * @return
	 * @throws LithiumServiceClientFactoryException 
	 */
	private String lookupReferrerCode(final String referrerGuidOrReferrerCode) throws LithiumServiceClientFactoryException {
		UserApiClient client = services.target(UserApiClient.class, "service-user", true);
		String guid = client.getUserGuidByShortGuid(referrerGuidOrReferrerCode).getData();

		if (guid == null || guid.trim().isEmpty()) {
			return referrerGuidOrReferrerCode;
		} else {
			return guid;
		}
	}
}
