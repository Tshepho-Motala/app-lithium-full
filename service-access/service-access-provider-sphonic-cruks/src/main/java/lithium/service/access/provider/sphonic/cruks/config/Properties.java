package lithium.service.access.provider.sphonic.cruks.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.service.access.sphonic.cruks")
@Configuration
public class Properties {
    FailedAttemptJob failedAttemptJob = new FailedAttemptJob();

    @Data
    public static class FailedAttemptJob {
        private Integer batchAttempts = 5;
        private Integer errorLoggingThreshold = 5;
        private Integer backoffThreshold = 5;
    }
}
