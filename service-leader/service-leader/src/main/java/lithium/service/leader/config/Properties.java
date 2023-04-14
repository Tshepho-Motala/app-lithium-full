package lithium.service.leader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.services.leader")
@Configuration
public class Properties {
	private CleanupJob cleanupJob = new CleanupJob();
	private Instance instance = new Instance();

	@Data
	public class CleanupJob {
		private int initialDelayMs = 240000;
		private int delayMs = 5000;
	}

	@Data
	public class Instance {
		private int keepAliveMs = 120000;
	}
}
