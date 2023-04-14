package lithium.service.accounting.provider.internal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.service.accounting")
@Configuration
public class Properties {
	private BalanceAdjustments balanceAdjustments = new BalanceAdjustments();
	private QueueRateLimiter queueRateLimiter = new QueueRateLimiter();

	@Data
	public class BalanceAdjustments {
		private boolean dispatchUserBalanceEventEnabled = true;
		private boolean summarizeEnabled = true;
		private boolean bypassSessionIdRequirement = false;
		private boolean sendCompletedSummaryAccountTransactionTypeEvent = false;
		private boolean summarizeDomainEnabled = true;
	}

	@Data
	public class QueueRateLimiter {
		private AuxLabel auxLabel = new AuxLabel();
		private TransactionLabel transactionLabel = new TransactionLabel();
	}

	@Data
	public class AuxLabel {
		private boolean enabled = false;
		private int minDelayMs = 50;
		private int maxDelayMs = 100;
	}

	@Data
	public class TransactionLabel {
		private boolean enabled = false;
		private int minDelayMs = 50;
		private int maxDelayMs = 100;
	}
}
