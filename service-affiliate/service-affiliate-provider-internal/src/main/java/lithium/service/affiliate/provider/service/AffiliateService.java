package lithium.service.affiliate.provider.service;

import java.security.Principal;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.Affiliate;
import lithium.service.affiliate.provider.data.entities.Domain;
import lithium.service.affiliate.provider.data.repositories.AffiliateRepository;
import lithium.service.affiliate.provider.data.repositories.DomainRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.AffiliateClient;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AffiliateService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired AffiliateRepository affiliateRepository;
	@Autowired DomainRepository domainRepository;
	@Autowired TokenStore tokenStore;

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
	
	public Domain findOrCreateDomain(final String domainName) {
		Domain domain = domainRepository.findByName(domainName).orElseGet(() -> domainRepository.save(Domain.builder().name(domainName).build()));
		return domain;
	}
	
	public LithiumTokenUtil getTokenUtil(Principal principal) {
		return LithiumTokenUtil.builder(tokenStore, principal).build();
	}
}
