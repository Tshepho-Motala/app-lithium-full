package lithium.service.user.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.user.services.SessionInactivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionInactivityTimeoutJob {
  @Autowired private LeaderCandidate leaderCandidate;
  @Autowired private SessionInactivityService service;

  @Scheduled(cron = "${lithium.services.user.session-inactivity-timeout-job.cron}")
  public void processInactiveSessionTimeout() {
    if (!leaderCandidate.iAmTheLeader()) {
      log.debug("I am not the leader");
      return;
    }

    service.processInactiveSessionTimeout();
  }
}
