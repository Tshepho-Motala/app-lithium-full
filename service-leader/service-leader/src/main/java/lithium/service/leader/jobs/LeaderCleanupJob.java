package lithium.service.leader.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.leader.services.LeaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaderCleanupJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private LeaderService service;

	@Scheduled(initialDelayString = "${lithium.services.leader.cleanup-job.initial-delay-ms:240000}",
			fixedDelayString = "${lithium.services.leader.cleanup-job.delay-ms:5000}")
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
		}
		service.cleanup();
	}
}
