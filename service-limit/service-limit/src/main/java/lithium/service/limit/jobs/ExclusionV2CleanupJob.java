package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.data.repositories.PlayerExclusionV2Repository;
import lithium.service.limit.services.ExclusionService;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
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
public class ExclusionV2CleanupJob {
	@Autowired private ExclusionService exclusionService;
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private LithiumMetricsService metrics;
	@Autowired private PlayerExclusionV2Repository repository;
	@Autowired private ServiceLimitConfigurationProperties properties;

	@Scheduled(cron="${lithium.service.limit.jobs.exclusion-cleanup.cron:0 0/1 * * * *}")
	public void process() throws Exception {
		log.debug("ExclusionV2CleanupJob running");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		metrics.timer(log,10000L, 10000L, 10000L).time("exclusionV2CleanupJob", (StopWatch sw) -> {
			int page = 0;
			boolean hasMore = true;
			while (hasMore) {
				PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getExclusionCleanup().getPageSize());
				Page<PlayerExclusionV2> playerExclusionPage = repository.findByExpiryDateNotNullAndExpiryDateBeforeOrderByExpiryDate(new Date(), pageRequest);
				log.debug("Found " + playerExclusionPage.getContent().size() + " entries. Page " + playerExclusionPage.getNumber() + " of " + playerExclusionPage.getTotalPages());
				sw.start("exclusionV2CleanupJob_pagerequest_"+page);
				for (PlayerExclusionV2 playerExclusion: playerExclusionPage.getContent()) {
					try {
						exclusionService.clear(playerExclusion.getPlayerGuid(), "System Scheduler", null);
					} catch (Exception ex) {
						log.warn("Unable to clear exclusion on player in job: " + playerExclusion.getPlayerGuid(), ex);
					}
				}
				sw.stop();
				page++;
				if (!playerExclusionPage.hasNext()) hasMore = false;
			}
		});
	}
}
