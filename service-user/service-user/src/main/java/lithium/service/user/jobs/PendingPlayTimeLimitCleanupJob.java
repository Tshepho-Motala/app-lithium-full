package lithium.service.user.jobs;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lithium.leader.LeaderCandidate;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Config;
import lithium.service.user.services.PlaytimeLimitsV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PendingPlayTimeLimitCleanupJob {

  @Autowired
  private LeaderCandidate leaderCandidate;
  @Autowired
  private ServiceUserConfigurationProperties properties;
  @Autowired
  private PlaytimeLimitsV2Service serviceV2;


  @TimeThisMethod
  @Scheduled(cron = "${lithium.services.user.jobs.pending-playtime-limit-cleanup.cron:0 * * * * *}")
  public void process() throws Exception {
    log.debug("PendingPlayTimeLimitCleanupJob is running");
    if (!leaderCandidate.iAmTheLeader()) {
      log.debug("I am not the leader.");
      return;
    }
    int page = 0;
    boolean hasMore = true;
    while (hasMore) {
      PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getPendingPlaytimeLimitCleanup().getPageSize());
      Page<PlayerPlaytimeLimitV2Config> allPending = serviceV2.findAllPending(pageRequest);
      log.info("PendingPlayTimeLimitCleanupJob Found " + allPending.getContent().size() + " entries. Page " + allPending.getNumber() + " of " + allPending.getTotalPages());
      SW.start("PendingPlayTimeLimitCleanup_pagerequest_" + page);
      for (PlayerPlaytimeLimitV2Config pl : allPending.getContent()) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (pl.getPendingConfigRevision().getEffectiveFrom().isBefore(now)) {
          log.debug(pl.getPendingConfigRevision().getEffectiveFrom() + " is before : " + now);
          log.info("Moving pending balance limit to current : " + pl);
          serviceV2.movePendingLimitToCurrent(pl);
        }
      }
      SW.stop();
      page++;
      if (!allPending.hasNext()) {
        hasMore = false;
      }
    }
  }

}
