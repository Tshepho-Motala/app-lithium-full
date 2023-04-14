package lithium.service.casino.provider.sportsbook.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.casino.provider.sportsbook.services.PendingReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PendingReservationJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private PendingReservationService service;

	@Scheduled(fixedDelayString = "${lithium.services.casino.provider.sportsbook.pending-reservation-job.delay-ms:300000}")
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.trace("I am not the leader.");
			return;
		}

		service.process();
	}
}
