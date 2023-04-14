
package lithium.service.casino.job;

import lithium.leader.LeaderCandidate;
import lithium.service.casino.service.BetTransactionDataCleanupService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "lithium.services.casino.bet-transactions-cleanup-job.enabled")
@Slf4j
public class CasinoBetDataCleanupJob {

    @Autowired private BetTransactionDataCleanupService transactionCleanupService;

    @Autowired private LeaderCandidate leaderCandidate;

    @Scheduled(cron = "${lithium.services.casino.bet-transactions-cleanup-job.job-execution-interval:0 0/3 * * * *}")
    public void processDelete() throws LithiumServiceClientFactoryException {
        if (!leaderCandidate.iAmTheLeader()) {
            log.trace("I am not the leader.");
            return;
        }
        transactionCleanupService.deleteOldData();
    }
}
