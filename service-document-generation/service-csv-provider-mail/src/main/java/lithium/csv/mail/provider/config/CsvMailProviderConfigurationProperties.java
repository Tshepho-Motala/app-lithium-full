package lithium.csv.mail.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lithium.service-csv-generation-mail")
@Data
public class CsvMailProviderConfigurationProperties {
    private static final int DEFAULT_JOB_PAGE_SIZE = 500;
    private Integer processingJobPageSize = DEFAULT_JOB_PAGE_SIZE;
}
