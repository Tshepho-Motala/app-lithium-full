package lithium.service.promo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.promo.config.ServicePromoConfigurationProperties;
import lithium.service.reward.client.stream.EnableGiveRewardStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.time.ZoneId;
import java.util.TimeZone;

@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
//@EnableMissionStatsStream
@EnableGiveRewardStream
@EnableTriggerBonusStream
//@EnableRewardClientService
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
//@EnableAccountingTransactionCompletedEvent
@EnableUserApiInternalClientService
@EnableChangeLogService
@EnableCustomHttpErrorCodeExceptions
public class ServicePromotionsApplication extends LithiumServiceApplication {

  @Autowired
  private ServicePromoConfigurationProperties configurationProperties;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServicePromotionsApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    setDefaultTimezone();
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  public void setDefaultTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(configurationProperties.getDefaultTimezone())));
  }

//  @Bean
//  public ObjectMapper mapper() {
//    ObjectMapper objectMapper = new ObjectMapper();
//    objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
//    return objectMapper;
//  }
}
