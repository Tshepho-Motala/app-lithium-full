package lithium.service.user.provider.threshold;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.stream.EnableAccountingSummaryAccountTransactionTypeCompletedEvent;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.provider.threshold.data.enums.NotificationType;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.service.user.provider.threshold.services.TypeService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Slf4j
@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
@EnableChangeLogService
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableProviderClient
@EnableLimitInternalSystemClient
@EnableNotificationStream
@EnableAccountingSummaryAccountTransactionTypeCompletedEvent(transactionTypeCodes = {"*_BET"})
@ComponentScan(basePackages = {"lithium.service"})
@EntityScan(
    value = {
        "lithium.service.user.provider.threshold.data.entities"
    }
)
@EnableJpaRepositories(
    value = {
        "lithium.service.user.provider.threshold.data.repositories"
    }
)
public class ServiceUserProviderThresholdApplication extends LithiumServiceApplication {

  @Autowired
  private NotificationStream notificationStream;

  @Autowired
  private TypeService typeService;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(lithium.service.user.provider.threshold.ServiceUserProviderThresholdApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);

    typeService.findOrCreate(Type.LIMIT_TYPE_LOSS);
    typeService.findOrCreate(Type.LIMIT_TYPE_WIN);

    notificationStream.registerNotificationType(NotificationType.THRESHOLD_WARNING.name());
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
