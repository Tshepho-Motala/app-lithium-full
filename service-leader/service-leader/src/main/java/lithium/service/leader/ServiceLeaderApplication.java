package lithium.service.leader;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.leader.config.Properties;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableConfigurationProperties(Properties.class)
@EnableLocaleContextProcessor
@EnableDomainClient
@EnableScheduling
public class ServiceLeaderApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceLeaderApplication.class, args);
    }
}
