package lithium.service.leaderboard;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.stream.event.EnableAccountingTransactionCompletedEvent;
import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.leaderboard.client.stream.EnableLeaderboardStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLeaderCandidate
@EnableLeaderboardStream
@EnableNotificationStream
@EnableTriggerBonusStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
@EnableAccountingTransactionCompletedEvent
@EnableScheduling
public class ServiceLeaderboardApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceLeaderboardApplication.class, args);
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
