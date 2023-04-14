package lithium.csv.cashier.transactions.provider.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lithium.service-csv-provider-cashier-transactions")
@Data
public class CsvCashierTransactionsProviderConfigurationProperties {

    private static final int DEFAULT_JOB_PAGE_SIZE = 500;

    private Integer processingJobPageSize = DEFAULT_JOB_PAGE_SIZE;

}
