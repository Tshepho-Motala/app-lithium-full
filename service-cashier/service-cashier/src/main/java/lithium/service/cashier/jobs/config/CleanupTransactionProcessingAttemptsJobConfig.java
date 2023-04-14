package lithium.service.cashier.jobs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lithium.service.cashier.jobs.cleanup-transaction-processing-attempt")
@Data
public class CleanupTransactionProcessingAttemptsJobConfig {
    private String cron;
    private int cleanupRetentionInDays = 90;
    private int batchSize = 100;
    private int pageCountPerOnce = 0;

}
