package lithium.service.games;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.accounting.client.stream.event.EnableAccountingTransactionCompletedEvent;
import lithium.service.cashier.client.event.EnableCashierFirstDepositEvent;
import lithium.service.casino.EnableProgressiveJackpotFeedClient;
import lithium.service.casino.client.stream.EnableFreeGameStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.games.client.service.EnableGameUserStatusClientService;
import lithium.service.games.services.GameChannelService;
import lithium.service.games.services.GameService;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableProviderClient
@EnableChangeLogService
@EnableDomainClient
@EnableGatewayExchangeStream
@EnableLithiumServiceClients
@EnableLimitInternalSystemClient
@EnableCustomHttpErrorCodeExceptions
@EnableAccountingTransactionCompletedEvent
@EnableUserApiInternalClientService
@EnableGameUserStatusClientService
@EnableLeaderCandidate
@EnableFreeGameStream
@EnableCashierFirstDepositEvent
@EnableRestTemplate
@EnableScheduling
@EnableProgressiveJackpotFeedClient
public class ServiceGamesApplication extends LithiumServiceApplication {
	@Autowired
	ServiceGamesInit initStuff;

	@Autowired
	GameService gameService;

	@Autowired
	GameChannelService gameChannelService;

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceGamesApplication.class, args);
	}

//	@Bean
//	public RestTemplate restTemplate(List<HttpMessageConverter<?>> messageConverters) {
//		return new RestTemplate(messageConverters);
//	}
//
//	@Bean
//	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
//		ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
//		arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
//		return arrayHttpMessageConverter;
//	}
//
//	private List<MediaType> getSupportedMediaTypes() {
//		List<MediaType> list = new ArrayList<MediaType>();
//		list.add(MediaType.IMAGE_JPEG);
//		list.add(MediaType.IMAGE_PNG);
//		list.add(MediaType.APPLICATION_OCTET_STREAM);
//		return list;
//	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		if (isLoadTestData()) {
			initStuff.init();
		}
		gameChannelService.createGameChannels();
	}
}
