package lithium.service.mail.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.mail.services.MailProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailProcessingJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private MailProcessService service;

	@Scheduled(cron = "${lithium.services.mail.processing-job-cron}")
	public void processStuckMail() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		service.processStuckMail();
	}

	@Scheduled(cron = "${lithium.services.mail.processing-job-cron}")
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		service.process();
	}
}
