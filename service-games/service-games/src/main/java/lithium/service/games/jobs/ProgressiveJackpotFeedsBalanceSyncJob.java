package lithium.service.games.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.games.services.ProgressiveJackpotFeedsBalanceSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProgressiveJackpotFeedsBalanceSyncJob {
    @Autowired private LeaderCandidate leaderCandidate;
    @Autowired private ProgressiveJackpotFeedsBalanceSyncService service;

    @Scheduled(fixedDelayString = "${lithium.services.games.progressive-jackpot-feeds-scheduler-in-milliseconds:30000}")
    public void sync() {
        if (leaderCandidate.iAmTheLeader()) {
            service.sync();
        }
    }
}
