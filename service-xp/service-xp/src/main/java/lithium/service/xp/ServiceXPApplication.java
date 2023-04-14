package lithium.service.xp;

import lithium.service.notifications.client.stream.EnableNotificationStream;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.service.accounting.client.stream.event.EnableAccountingTransactionCompletedEvent;
import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.raf.client.stream.EnableRAFConversionStream;
import lithium.service.xp.services.StatusService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableTriggerBonusStream
@EnableMissionStatsStream
@EnableGatewayExchangeStream
@EnableLithiumServiceClients
@EnableAccountingTransactionCompletedEvent
@EnableDomainClient
@EnableRAFConversionStream
@EnableNotificationStream
public class ServiceXPApplication extends LithiumServiceApplication {
	@Autowired
	private Init initStuff;
	
	@Autowired
	private StatusService statusService;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceXPApplication.class, args);
	}
	
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		createStatuses();
		
		if (isLoadTestData()) {
			initStuff.init();
		}
	}
	
	private void createStatuses() {
		statusService.findOrCreate("ACTIVE", "An active state");
		statusService.findOrCreate("INACTIVE", "An inactive state");
		statusService.findOrCreate("ARCHIVE", "An archived state");
	}
}
