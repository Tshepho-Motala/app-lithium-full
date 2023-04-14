package lithium.csv.cashier.transactions.provider;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.csv.cashier.transactions.provider.config.CsvCashierTransactionsProviderConfigurationProperties;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.csv.provider.EnableCsvGenerationClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@LithiumService
@EnableLithiumMetrics
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableConfigurationProperties(CsvCashierTransactionsProviderConfigurationProperties.class)
@EnableCsvGenerationClient
public class ServiceCsvCashierTransactionsProviderApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCsvCashierTransactionsProviderApplication.class, args);
    }
}
