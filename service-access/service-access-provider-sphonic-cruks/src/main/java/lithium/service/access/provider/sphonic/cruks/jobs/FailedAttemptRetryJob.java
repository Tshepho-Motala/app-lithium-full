package lithium.service.access.provider.sphonic.cruks.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.access.provider.sphonic.cruks.services.FailedAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FailedAttemptRetryJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private FailedAttemptService service;

	@Scheduled(fixedDelayString = "${lithium.service.access.sphonic.cruks.failed-attempt-job.delay-ms:60000}")
	public void process() {
		log.debug("FailedAttemptRetryJob Runnning.");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		service.retry();
	}
}
