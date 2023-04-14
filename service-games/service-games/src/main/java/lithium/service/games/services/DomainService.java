package lithium.service.games.services;

import java.util.ArrayList;
import java.util.List;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.DomainRevisionLabelValue;
import lithium.service.games.data.repositories.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lithium.metrics.LithiumMetricsService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lombok.extern.slf4j.Slf4j;

import static lithium.service.domain.client.DomainSettings.CASINO_ALLOW_TESTACCOUNT_JACKPOT_GAMES;

@Service
@Slf4j
public class DomainService {
	
	@Autowired
	private LithiumServiceClientFactory services;

	@Autowired 
	private LithiumMetricsService metrics;

	@Autowired
	private CachingDomainClientService cachingDomainClientService;

	public DomainClient getDomainService() throws Exception {
		DomainClient cl = null;

		cl = services.target(DomainClient.class,"service-domain", true);

		return cl;
	}

	@Cacheable(cacheNames="lithium.service.games.services.findAncestralDomains", unless="#result.isEmpty()")
	public List<Domain> findAncestralDomains(String domainName) throws Exception {
		
		if(domainName.contentEquals("default")) return new ArrayList<Domain>();
		
		List<Domain> ld = getDomainService().ancestors(domainName).getData();
		return ld;
	}

	public Boolean casinoAllowTestAccountJackpotGames(String domainName) {
		String domainSettingStringValue = cachingDomainClientService.getDomainSetting(domainName, CASINO_ALLOW_TESTACCOUNT_JACKPOT_GAMES);
		return Boolean.valueOf(domainSettingStringValue);
	}

}
