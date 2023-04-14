package lithium.service.affiliate.provider.service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.client.AffiliateProviderClient;
import lithium.service.affiliate.client.exception.AdNotFoundException;
import lithium.service.affiliate.client.exception.AffiliateNotFoundException;
import lithium.service.affiliate.client.exception.AffiliatePlayerAlreadyExistsException;
import lithium.service.affiliate.client.exception.AffiliateProviderNotFoundException;
import lithium.service.affiliate.client.exception.UserNotFoundException;
import lithium.service.affiliate.client.exception.UserProviderNotFoundException;
import lithium.service.affiliate.client.objects.AffiliatePlayerBasic;
import lithium.service.affiliate.provider.data.entities.Affiliate;
import lithium.service.affiliate.provider.data.entities.AffiliatePlayer;
import lithium.service.affiliate.provider.data.entities.AffiliatePlayerRevision;
import lithium.service.affiliate.provider.data.repositories.AffiliatePlayerRepository;
import lithium.service.affiliate.provider.data.repositories.AffiliatePlayerRevisionRepository;
import lithium.service.affiliate.provider.data.repositories.AffiliateRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.AffiliateClient;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AffiliatePlayerService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired AffiliateRepository affiliateRepository;
	@Autowired AffiliatePlayerRevisionRepository affiliatePlayerAffiliateHistoryRepository;
	@Autowired TokenStore tokenStore;
	@Autowired AffiliatePlayerRepository affiliatePlayerRepository;

	public Affiliate findOrCreate(String userGuid) {
		Affiliate affiliate = affiliateRepository.findByUserGuid(userGuid);
		if (affiliate == null) {
			affiliate = affiliateRepository.save(Affiliate.builder().userGuid(userGuid).build());
		}
		return affiliate;
	}
	
	public AffiliateClient getAffiliateClient() {
		AffiliateClient client = null;
		try {
			client = services.target(AffiliateClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}
	
	public LithiumTokenUtil getTokenUtil(Principal principal) {
		return LithiumTokenUtil.builder(tokenStore, principal).build();
	}

	public AffiliatePlayer findUserByGuid(String guid) {
		return affiliatePlayerRepository.findByPlayerGuid(guid).orElse(null);
	}
	
	public AffiliatePlayer addAffiliatePlayer(AffiliatePlayerBasic player) throws AffiliateNotFoundException, AffiliateProviderNotFoundException, AdNotFoundException, UserProviderNotFoundException, UserNotFoundException, AffiliatePlayerAlreadyExistsException {
		
		if (affiliatePlayerRepository.findByPlayerGuid(player.getPlayerGuid()).isPresent()) throw new AffiliatePlayerAlreadyExistsException();
		
		Affiliate affl = affiliateRepository.findByGuid(player.getPrimaryGuid()).orElseThrow(() -> new AffiliateNotFoundException());
		
//		AffiliateProviderClient provider = getAffiliateProviderClient(affl).orElseThrow(() -> new AffiliateProviderNotFoundException());

//		Response<Ad> addResponse = provider.findByGuid(player.getSecondaryGuid());
//		if (addResponse.getStatus() != Status.OK) throw new AdNotFoundException();
		
		UserApiInternalClient userClient = getUserApiInternalClient().orElseThrow(() -> new UserProviderNotFoundException());
		Response<User> userResponse = userClient.getUser(player.getPlayerGuid());
		if (userResponse.getStatus() != Status.OK) throw new UserNotFoundException();
		
		AffiliatePlayer afflPlayer = AffiliatePlayer.builder()
						.playerGuid(player.getPlayerGuid())
						.build();
		afflPlayer = affiliatePlayerRepository.save(afflPlayer);
		
		updatePlayerAffiliation(afflPlayer, affl, player.getPrimaryGuid(), player.getSecondaryGuid(), player.getTertiaryGuid(), player.getQuaternaryGuid());
		
		return afflPlayer;
	}
	
	@Transactional
	private void updatePlayerAffiliation(AffiliatePlayer player, final Affiliate affiliate, final String primaryGuid, final String secondaryGuid, final String tertiaryGuid, final String quaternaryGuid) {
		
		AffiliatePlayerRevision playerAfflHistOld = null;
		if (player.getCurrent() != null) {
			playerAfflHistOld = player.getCurrent();
			playerAfflHistOld.setArchiveDate(new Date());
			playerAfflHistOld.setCurrent(false);
			playerAfflHistOld = affiliatePlayerAffiliateHistoryRepository.save(playerAfflHistOld);
		}
		
		AffiliatePlayerRevision playerAfflHist = AffiliatePlayerRevision.builder()
				.affiliate(affiliate)
				.affiliatePlayerId(player.getId())
				.effectiveDate(playerAfflHistOld == null ? null : playerAfflHistOld.getArchiveDate())
				.primaryGuid(primaryGuid)
				.secondaryGuid(secondaryGuid)
				.tertiaryGuid(tertiaryGuid)
				.quaternaryGuid(quaternaryGuid)
				.current(true)
				.build();

		playerAfflHist = affiliatePlayerAffiliateHistoryRepository.save(playerAfflHist);
		
		player.setCurrent(playerAfflHist);
		player = affiliatePlayerRepository.save(player);
	}
	
	public AffiliatePlayer editAffiliatePlayer(AffiliatePlayer player) throws UserNotFoundException, AffiliateProviderNotFoundException, AdNotFoundException, AffiliateNotFoundException {
		
		AffiliatePlayer repoPlayer = affiliatePlayerRepository.findByPlayerGuid(player.getPlayerGuid()).orElseThrow(() -> new UserNotFoundException());
		
		Affiliate affl = affiliateRepository.findByGuid(player.getCurrent().getAffiliate().getGuid()).orElseThrow(() -> new AffiliateNotFoundException());
		
		AffiliateProviderClient provider = getAffiliateProviderClient(player.getCurrent().getAffiliate()).orElseThrow(() -> new AffiliateProviderNotFoundException());
		
		boolean dirty = false;
		
		if (!repoPlayer.getCurrent().getSecondaryGuid().equals(player.getCurrent().getSecondaryGuid())) {
		//	Response<Ad> addResponse = provider.findByGuid(player.getCurrent().getSecondaryGuid());
		//	if (addResponse.getStatus() != Status.OK) throw new AdNotFoundException();
			repoPlayer.getCurrent().setSecondaryGuid(player.getCurrent().getSecondaryGuid());
			dirty = true;
		}
		
		if (!repoPlayer.getCurrent().getTertiaryGuid().equals(player.getCurrent().getTertiaryGuid())) {
			repoPlayer.getCurrent().setTertiaryGuid(player.getCurrent().getTertiaryGuid());
			dirty = true;
		}
		
		if (!repoPlayer.getCurrent().getQuaternaryGuid().equals(player.getCurrent().getQuaternaryGuid())) {
			repoPlayer.getCurrent().setQuaternaryGuid(player.getCurrent().getQuaternaryGuid());
			dirty = true;
		}
		
		if (!repoPlayer.getCurrent().getAffiliate().getGuid().equals(player.getCurrent().getAffiliate().getGuid())) {
			repoPlayer.getCurrent().setPrimaryGuid(affl.getGuid());
			dirty = false;
		}
		
		if (dirty) {
			updatePlayerAffiliation(repoPlayer, affl, repoPlayer.getCurrent().getPrimaryGuid(), repoPlayer.getCurrent().getSecondaryGuid(), repoPlayer.getCurrent().getTertiaryGuid(), repoPlayer.getCurrent().getQuaternaryGuid());
			dirty = false;
		}
		
		return repoPlayer;
	}
	
	public Optional<AffiliateProviderClient> getAffiliateProviderClient(Affiliate affiliate) {
		
		//TODO: do some lookups to providers and get relevant implementation based on affiliate passed as parameter. This will be domain based
		String providerUrl = "service-affiliate-provider-internal";
		
		return getClient(AffiliateProviderClient.class, providerUrl);
	}
	
	public Optional<UserApiInternalClient> getUserApiInternalClient() {
		return getClient(UserApiInternalClient.class, "service-user");
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
