package lithium.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.access.client.EnableAccessService;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.cashier.client.event.EnableCashierFirstDepositEvent;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.limit.client.stream.EnableAutoRestrictionTriggerStream;
import lithium.service.limit.client.stream.EnablePromotionRestrictionTriggerStream;
import lithium.service.limit.client.stream.EnableUserRestrictionTriggerStream;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.client.EnableNotificationsInternalSystemService;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.stats.client.stream.EnableStatsStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.services.StatusService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Slf4j
@LithiumService
@EnableFeignClients
@EnableScheduling
@EnableMailStream
@EnableStatsStream
@EnableDomainClient
@EnableAccessService
@EnableProviderClient
@EnableLeaderCandidate
@EnableChangeLogService
@EnableRestTemplate
@EnableTranslationsStream
@EnableMissionStatsStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
@EnableAccountingClientService
@EnableLimitInternalSystemClient
@EnableAutoRestrictionTriggerStream
@EnableUserRestrictionTriggerStream
@EnableNotificationsInternalSystemService
@EnablePromotionRestrictionTriggerStream
@EnableCashierFirstDepositEvent
public class ServiceUserApplication extends LithiumServiceApplication {
	@Autowired Init initStuff;
	@Autowired StatusService statusService;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceUserApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);

    if (isLoadTestData()) {
      initStuff.init();
    }
    //TODO: Rework the manual adjustment listing to be user friendly and not use a cannibalized table from accounting
    initStuff.setupAccountCodesFromEnum();
    initStuff.initUserLinkTypes();
    initStuff.initGranularity();
    statusService.setupFromEnums();
  }

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    return new Jackson2JsonMessageConverter(objectMapper);
	}

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		HttpMessageConverter<Object> jackson = new MappingJackson2HttpMessageConverter(mapper);

		RestTemplate restTemplate = new RestTemplate();

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);
		restTemplate.setRequestFactory(requestFactory);

		return restTemplate;
	}
}
