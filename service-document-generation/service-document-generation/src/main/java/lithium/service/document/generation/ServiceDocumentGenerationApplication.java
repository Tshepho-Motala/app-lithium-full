package lithium.service.document.generation;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.document.generation.config.DocumentGenerationConfigurationProperties;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableConfigurationProperties({DocumentGenerationConfigurationProperties.class})
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableScheduling
public class ServiceDocumentGenerationApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceDocumentGenerationApplication.class, args);
	}

	@EventListener
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
