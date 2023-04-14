package lithium.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.ToString;

@ConfigurationProperties(prefix="lithium.metrics")
@Data
@ToString
public class LithiumMetricsConfigurationProperties {

	private long errorThresholdMillis = 1000;
	private long warningThresholdMillis = 800;
	private long infoThresholdMillis = 800;
}
