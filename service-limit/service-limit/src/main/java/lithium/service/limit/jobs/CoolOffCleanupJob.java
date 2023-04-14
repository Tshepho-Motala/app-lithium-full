package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.repositories.PlayerCoolOffRepository;
import lithium.service.limit.services.CoolOffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;

@Component
@Slf4j
public class CoolOffCleanupJob {
	@Autowired private ServiceLimitConfigurationProperties properties;
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private LithiumMetricsService metrics;
	@Autowired private PlayerCoolOffRepository repository;
	@Autowired private CoolOffService coolOffService;

	@Scheduled(cron="${lithium.service.limit.jobs.cool-off-cleanup.cron:0 0/1 * * * *}")
	public void process() throws Exception {
		log.debug("CoolOffCleanupJob running");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		metrics.timer(log,10000L, 10000L, 10000L).time("coolOffCleanupJob", (StopWatch sw) -> {
			int page = 0;
			boolean hasMore = true;
			while (hasMore) {
				PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getCoolOffCleanup().getPageSize());
				Page<PlayerCoolOff> playerCoolOffPage = repository.findByExpiryDateBeforeOrderByExpiryDate(new Date(), pageRequest);
				log.debug("Found " + playerCoolOffPage.getContent().size() + " entries. Page " + playerCoolOffPage.getNumber() + " of " + playerCoolOffPage.getTotalPages());
				sw.start("cooloffCleanupJob_pagerequest_"+page);
				for (PlayerCoolOff playerCoolOff: playerCoolOffPage.getContent()) {
					try {
					coolOffService.clear(playerCoolOff.getPlayerGuid(), "System Scheduler", null);
					} catch (Exception ex) {
							log.warn("Unable to clear exclusion on player in job: " + playerCoolOff.getPlayerGuid(), ex);
					}
				}
				sw.stop();
				page++;
				if (!playerCoolOffPage.hasNext()) hasMore = false;
			}
		});
	}
}
