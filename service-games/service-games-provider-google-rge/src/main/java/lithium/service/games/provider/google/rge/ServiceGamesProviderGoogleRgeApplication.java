package lithium.service.games.provider.google.rge;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableDomainClient
@EnableLithiumServiceClients
@EnableProviderClient
@EnableLeaderCandidate
@EnableRestTemplate
@EnableScheduling
public class ServiceGamesProviderGoogleRgeApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceGamesProviderGoogleRgeApplication.class, args);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
