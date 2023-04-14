package lithium.service.reward.jobs;

import java.util.concurrent.atomic.AtomicBoolean;
import lithium.leader.LeaderCandidate;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.config.ServiceRewardConfigurationProperties;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.service.PlayerRewardHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpiringRewardsCleanupJob {

  @Autowired
  private LeaderCandidate leaderCandidate;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private PlayerRewardHistoryService playerRewardHistoryService;
  @Autowired
  private ServiceRewardConfigurationProperties properties;
  @Autowired
  private LithiumServiceClientFactory clientFactory;
  @Autowired
  private ModelMapper modelMapper;

  @TimeThisMethod
  @Schedules( value = {@Scheduled( initialDelay = 120000, fixedDelay = Long.MAX_VALUE ),
      @Scheduled( cron = "${lithium.service.reward.jobs.expiring-rewards-cleanup.cron:0 * * * * *}" )} )
  public void process()
  throws Exception
  {
    log.debug("ExpiringRewardsCleanupJob is running");
    if (!leaderCandidate.iAmTheLeader()) {
      log.debug("I am not the leader.");
      return;
    }
    int page = 0;
    boolean hasMore = true;
    while (hasMore) {
      PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getExpiringRewardsCleanup().getPageSize());
      Page<PlayerRewardHistory> expiredRewards = playerRewardHistoryService.findExpiredRewards(pageRequest);
      log.debug(
          "Found " + expiredRewards.getContent().size() + " entries. Page " + expiredRewards.getNumber() + " of " + expiredRewards.getTotalPages());
      SW.start("ExpiringRewardsCleanupJob_pagerequest_" + page);

      for (PlayerRewardHistory playerRewardHistory: expiredRewards.getContent()) {
        try {
          log.debug("PlayerRewardHistory: {}", playerRewardHistory);
          playerRewardHistoryService.cancelPlayerReward(playerRewardHistory);
        }
        catch (Exception e) {
          log.error("Failed to cancel player reward: {}, reason: {}", playerRewardHistory, e);
        }
      }
      SW.stop();
      page++;
      if (!expiredRewards.hasNext()) {
        hasMore = false;
      }
    }
  }
}