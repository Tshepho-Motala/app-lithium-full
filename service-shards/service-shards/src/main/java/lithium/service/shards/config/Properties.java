package lithium.service.shards.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.services.shards")
@Configuration
public class Properties {
	private boolean cleanupJobEnabled = true;
	private int cleanupJobDelayMs = 120000;
	private int cleanupMs = 3600000;
	private int keepAliveMs = 600000;
}
