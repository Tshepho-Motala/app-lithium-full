package lithium.csv.provider.threshold.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lithium.service-csv-generation-threshold")
@Data
public class CsvThresholdProviderConfigurationProperties {
    private static final int DEFAULT_JOB_PAGE_SIZE = 500;
    private Integer processingJobPageSize = DEFAULT_JOB_PAGE_SIZE;
}
