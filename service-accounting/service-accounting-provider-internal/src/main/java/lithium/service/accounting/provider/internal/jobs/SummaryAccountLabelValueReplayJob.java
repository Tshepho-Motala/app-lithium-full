package lithium.service.accounting.provider.internal.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.accounting.provider.internal.conditional.SummaryAccountLabelValueReplayConditional;
import lithium.service.accounting.provider.internal.services.SummaryAccountLabelValueReplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Conditional(SummaryAccountLabelValueReplayConditional.class)
@Slf4j
public class SummaryAccountLabelValueReplayJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private SummaryAccountLabelValueReplayService service;

	@Scheduled(fixedDelayString = "${lithium.service.accounting.summary.account.label-value.replay.delay-ms:5000}")
	public void process() throws Exception {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		service.process();
	}
}
