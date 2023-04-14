package lithium.service.accounting.domain.v2.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.accounting.domain.v2.services.SummaryReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.v2.reconciliation.enabled", havingValue = "true")
@Component
@Slf4j
public class SummaryReconciliationJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private SummaryReconciliationService service;

	private static final String SHARD_KEY = "SummaryReconciliationJob";

	// This has to be single threaded.
	@Scheduled(cron = "${lithium.service.accounting.domain.summary.v2.reconciliation.cron:*/5 * * * * *}")
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		try {
			service.process(SHARD_KEY, null, null);
		} catch (Exception e) {
			log.error("Summary reconciliation process caught an error. Modifications rolled back. The same date"
					+ " will be attempted again on the next scheduled run | {}", e.getMessage(), e);
		}
	}
}
