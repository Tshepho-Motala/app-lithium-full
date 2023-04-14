package lithium.csv.casino.provider;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.csv.casino.provider.config.CsvProviderCasinoConfigurationProperties;
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
@EnableConfigurationProperties(CsvProviderCasinoConfigurationProperties.class)
@EnableCsvGenerationClient
public class ServiceCsvProviderCasinoApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCsvProviderCasinoApplication.class, args);
    }
}
