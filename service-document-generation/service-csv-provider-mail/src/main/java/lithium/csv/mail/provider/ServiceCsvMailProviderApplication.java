package lithium.csv.mail.provider;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.csv.mail.provider.config.CsvMailProviderConfigurationProperties;
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
@EnableConfigurationProperties(CsvMailProviderConfigurationProperties.class)
@EnableCsvGenerationClient
public class ServiceCsvMailProviderApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCsvMailProviderApplication.class, args);
    }
}
