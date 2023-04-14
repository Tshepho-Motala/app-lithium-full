package lithium.service.access.provider.sphonic.iban;

import lithium.client.changelog.EnableChangeLogService;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.access.provider.sphonic.iban.config.ServiceAccessProviderSphonicIBANConfigurationProperties;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableDomainClient
@EnableProviderClient
@EnableUserApiInternalClientService
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableRestTemplate
@EnableChangeLogService
@EnableScheduling
@EnableConfigurationProperties(ServiceAccessProviderSphonicIBANConfigurationProperties.class)
@ComponentScan(basePackages = "lithium.service.access.provider.sphonic")
@EntityScan(
    value = {
        "lithium.service.access.provider.sphonic.data.entities"
    }
)
@EnableJpaRepositories(
    value = {
        "lithium.service.access.provider.sphonic.data.repositories",
        "lithium.service.access.provider.sphonic.iban.storage.repositories"
    }
)
public class ServiceAccessProviderSphonicIBANApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceAccessProviderSphonicIBANApplication.class, args);
    }
}
