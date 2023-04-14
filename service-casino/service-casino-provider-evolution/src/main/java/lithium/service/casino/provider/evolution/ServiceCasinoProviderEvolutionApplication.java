package lithium.service.casino.provider.evolution;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@ComponentScan(basePackages = "lithium.service.casino.provider")
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
public class ServiceCasinoProviderEvolutionApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCasinoProviderEvolutionApplication.class, args);
    }
}
