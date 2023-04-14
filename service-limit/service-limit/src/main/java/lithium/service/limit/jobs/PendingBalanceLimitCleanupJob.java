package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.services.BalanceLimitService;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PendingBalanceLimitCleanupJob {

    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private ServiceLimitConfigurationProperties properties;
    @Autowired
    private BalanceLimitService balanceLimitService;
    @Autowired
    CachingDomainClientService cachingDomainClientService;

    @TimeThisMethod
    @Scheduled(cron = "${lithium.service.limit.jobs.pending-balance-limit-cleanup.cron:0 * * * * *}")
    public void process() throws Exception {
        log.debug("PendingBalanceLimitCleanupJob is running");
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        int page = 0;
        boolean hasMore = true;
        while (hasMore) {
            PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getPendingBalanceLimitCleanup().getPageSize());
            Page<PlayerLimit> allPending = balanceLimitService.findAllPending(pageRequest);
            log.info("Found " + allPending.getContent().size() + " entries. Page " + allPending.getNumber() + " of " + allPending.getTotalPages());
            SW.start("PendingBalanceLimitCleanup_pagerequest_" + page);
            for (PlayerLimit pl : allPending.getContent()) {
                Integer updateDelayInHours = balanceLimitService.getPendingBalanceLimitUpdateDelay(pl.getDomainName());

                if (new DateTime(pl.getCreatedDate(), DateTimeZone.getDefault()).plusHours(updateDelayInHours).isBeforeNow()) {
                    log.debug(new DateTime(pl.getCreatedDate(), DateTimeZone.getDefault()).plusHours(updateDelayInHours) + " is before : " + DateTime.now());
                    log.info("Moving pending balance limit to current : " + pl);
                    balanceLimitService.movePendingLimitToCurrent(pl, User.SYSTEM_GUID);
                }
            }
            SW.stop();
            page++;
            if (!allPending.hasNext()) hasMore = false;
        }
    }

}
