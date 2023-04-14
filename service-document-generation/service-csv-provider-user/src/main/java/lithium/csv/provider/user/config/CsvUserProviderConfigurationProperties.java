package lithium.csv.provider.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lithium.service-csv-generation-user")
public class CsvUserProviderConfigurationProperties {
    private static final int DEFAULT_JOB_PAGE_SIZE = 500;
    private Integer processingJobPageSize = DEFAULT_JOB_PAGE_SIZE;
}
