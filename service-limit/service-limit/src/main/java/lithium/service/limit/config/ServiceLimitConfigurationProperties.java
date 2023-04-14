package lithium.service.limit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "lithium.service.limit")
@Data
public class ServiceLimitConfigurationProperties {
	private Jobs jobs = new Jobs();
	private List<PublicApiAuthorization> publicApiAuthorizations = new ArrayList<>();
	private Queues queues = new Queues();

	@Data
	public static class Jobs {
		private ExclusionCleanup exclusionCleanup;
		private CoolOffCleanup coolOffCleanup;
		private PendingDepositLimitCleanup pendingDepositLimitCleanup;
		private PendingBalanceLimitCleanup pendingBalanceLimitCleanup;
	}

	@Data
	public static class ExclusionCleanup {
		private String cron;
		private int pageSize = 1000;
	}

	@Data
	public static class CoolOffCleanup {
		private String cron;
		private int pageSize = 1000;
	}

	@Data
	public static class PendingDepositLimitCleanup {
		private String cron;
		private int pageSize = 1000;
	}

	@Data
	public static class PendingBalanceLimitCleanup {
		private String cron;
		private int pageSize = 1000;
	}

	@Data
	public static class PublicApiAuthorization {
		private String id;
		private String secretKey;
	}

	@Data
	public static class Queues {
		private PromotionRestrictionTrigger promotionRestrictionTrigger;
	}

	@Data
	public static class PromotionRestrictionTrigger {
		private int maxDlqRetries;
		private int backOffOptionInterval;
		private int backOffOptionMaxInterval;
		private double backOffOptionMultiplier;
		private int maxAttempts;
		private int concurrentConsumers;
		private boolean defaultRequeueRejected;
		private int messageDelay;
	}
}
