package lithium.service.geo.services;

import lithium.leader.LeaderCandidate;
import lithium.service.geo.config.ServiceGeoConfigurationProperties;
import lithium.service.geo.data.repositories.CountryRepository;
import lithium.service.geo.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MaxMindSynchronizer {

	@Autowired LeaderCandidate candidate;
	@Autowired ServiceGeoConfigurationProperties properties;
	@Autowired CountryRepository countryRepository;
	@Autowired MaxMindLookupService maxMindLookupService;

	@Scheduled(cron="${lithium.service.geo.max-mind-update-cron}")
	public void download() throws Exception {
		if (!properties.getMaxMindUpdateEnabled()) return;

		//The force option needs to be false to ensure we only download DB updates
		maxMindLookupService.loadDb(Boolean.FALSE, Boolean.TRUE);
	}
	
}
