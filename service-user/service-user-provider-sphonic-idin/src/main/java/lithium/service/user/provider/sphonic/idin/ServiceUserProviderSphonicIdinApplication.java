package lithium.service.user.provider.sphonic.idin;

import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableFeignClients
@EnableLithiumServiceClients
@EnableDomainClient
@EnableProviderClient
@EnableLeaderCandidate
@EnableRestTemplate
@EnableScheduling
@EnableConfigurationProperties
@EnableChangeLogService
@ComponentScan(basePackages = {"lithium.service.user.provider.sphonic.idin", "lithium.service.access.provider.sphonic"})
@EntityScan(
        value = {
                "lithium.service.access.provider.sphonic.data.entities",
                "lithium.service.user.provider.sphonic.idin.storage.entities"
        }
)
@EnableJpaRepositories(
        value = {
                "lithium.service.access.provider.sphonic.data.repositories",
                "lithium.service.user.provider.sphonic.idin.storage.repositories"
        }
)
@SpringBootApplication
public class ServiceUserProviderSphonicIdinApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserProviderSphonicIdinApplication.class, args);
    }


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
