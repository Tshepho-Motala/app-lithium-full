package lithium.service.stats;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.stats.stream.DomainStatsOutputQueue;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableGatewayExchangeStream
@EnableBinding({DomainStatsOutputQueue.class})
public class ServiceStatsApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceStatsApplication.class, args);
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
