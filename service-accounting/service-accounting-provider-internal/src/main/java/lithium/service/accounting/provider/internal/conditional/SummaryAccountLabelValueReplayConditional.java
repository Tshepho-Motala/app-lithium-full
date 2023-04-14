package lithium.service.accounting.provider.internal.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class SummaryAccountLabelValueReplayConditional extends SummariesConditional {
	SummaryAccountLabelValueReplayConditional() {
		super();
	}

	@ConditionalOnProperty(name = "lithium.service.accounting.summary.account.label-value.replay.enabled",
			havingValue = "true")
	static class SummaryAccountLabelValueReplayCondition {
	}
}
