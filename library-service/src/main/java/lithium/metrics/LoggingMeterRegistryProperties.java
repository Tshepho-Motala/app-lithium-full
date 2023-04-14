package lithium.metrics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix="management.metrics.export.logging")
@Data
public class LoggingMeterRegistryProperties {

    private boolean enabled = true;

    private Duration step = Duration.ofMinutes(1);
}
