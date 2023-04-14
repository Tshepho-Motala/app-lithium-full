package lithium.csv.provider.user;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.csv.provider.user.config.CsvUserProviderConfigurationProperties;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.csv.provider.EnableCsvGenerationClient;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@LithiumService
@EnableLithiumMetrics
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableCsvGenerationClient
@EnableLoginEventClientService
@EnableConfigurationProperties(CsvUserProviderConfigurationProperties.class)
public class ServiceCsvUserProviderApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCsvUserProviderApplication.class, args);
    }
}
