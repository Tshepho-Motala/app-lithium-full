package lithium.service.user.search;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.stream.event.EnableAccountingTransactionCompletedEvent;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
@EnableChangeLogService
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
@EnableLimitInternalSystemClient
@EnableAccountingTransactionCompletedEvent
@EntityScan(
    value = {
        "lithium.service.user.data.entities",
        "lithium.service.user.search.data.entities",
        "lithium.service.cashier.data.entities"
    }
)
@EnableJpaRepositories(
    value = {
        "lithium.service.user.search.data.repositories"
    }
)
public class ServiceUserSearchApplication extends LithiumServiceApplication {

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceUserSearchApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
