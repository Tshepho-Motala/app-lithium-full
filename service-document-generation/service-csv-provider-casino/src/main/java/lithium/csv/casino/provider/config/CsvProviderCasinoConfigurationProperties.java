package lithium.csv.casino.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lithium.service-csv-provider-casino")
@Data
public class CsvProviderCasinoConfigurationProperties {
    private static final int DEFAULT_JOB_PAGE_SIZE = 500;
    private Integer processingJobPageSize = DEFAULT_JOB_PAGE_SIZE;
}
