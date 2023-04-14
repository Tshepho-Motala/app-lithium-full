package lithium.service.casino.search;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
@EntityScan(
    value = {
        "lithium.service.casino.data.entities"
    }
)
@EnableJpaRepositories(
    value = {
        "lithium.service.casino.search.data.repositories"
    }
)
public class ServiceCasinoSearchApplication extends LithiumServiceApplication {

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceCasinoSearchApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
  }
}
