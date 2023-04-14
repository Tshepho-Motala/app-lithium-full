package lithium.service.user.threshold;

import java.util.Arrays;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.accounting.stream.EnableAccountingSummaryAccountTransactionTypeCompletedEvent;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.service.NotificationService;
import lithium.service.user.threshold.service.TypeService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;


@Slf4j
@LithiumService
@EnableRestTemplate
@EnableDomainClient
@EnableProviderClient
@EnableLeaderCandidate
@EnableChangeLogService
@EnableTranslationsStream
@EnableNotificationStream
@EnableLithiumServiceClients
@EnableAccountingClientService
@EnableLimitInternalSystemClient
@EnableAccountingSummaryAccountTransactionTypeCompletedEvent( transactionTypeCodes = {"*_BET", "CASHIER_*"} )
public class ServiceUserThresholdApplication extends LithiumServiceApplication {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private TypeService typeService;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(lithium.service.user.threshold.ServiceUserThresholdApplication.class, args);
  }

  @Override
  @EventListener
  public void startup(ApplicationStartedEvent e)
  throws Exception
  {
    super.startup(e);
    Arrays.stream(EType.values()).forEach(et -> typeService.findOrCreate(et));

    notificationService.registerAndCreateNotifications();
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
