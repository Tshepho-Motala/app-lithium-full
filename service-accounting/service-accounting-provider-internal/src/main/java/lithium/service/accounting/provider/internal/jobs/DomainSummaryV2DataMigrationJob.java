package lithium.service.accounting.provider.internal.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.accounting.provider.internal.services.DomainSummaryV2DataMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.v2.data-migration.enabled", havingValue = "true")
@Component
@Slf4j
public class DomainSummaryV2DataMigrationJob {

    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private DomainSummaryV2DataMigrationService domainSummaryV2DataMigrationService;

    @Scheduled(cron = "${lithium.service.accounting.domain.summary.v2.data-migration.cron:*/10 * * * * *}")
    public void process() {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }

        try {
            domainSummaryV2DataMigrationService.process();
        } catch (Exception e) {
            log.error("An error occurred while executing the domain summary v2 data migration process | {}", e.getMessage(), e);
        }
    }

}
