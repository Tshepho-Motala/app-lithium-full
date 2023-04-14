package lithium.service.games.provider.google.rge.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.games.provider.google.rge.services.GoogleAuthenticationService;
import lithium.service.games.provider.google.rge.services.RecommendedGamesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleAuthenticationJob {

    @Autowired
    private LeaderCandidate leader;

    @Autowired
    private GoogleAuthenticationService googleAuthenticationService;

    @Scheduled(fixedDelayString = "${lithium.services.games.provider.google.rge.google-authentication-scheduler-in-millis:60000}")
    public void process() {
        if (!leader.iAmTheLeader()) {
            log.info("I am not the leader");
            return;
        }

        googleAuthenticationService.evictExpiredTokens();
        googleAuthenticationService.authenticateAllDomains();
    }

}
