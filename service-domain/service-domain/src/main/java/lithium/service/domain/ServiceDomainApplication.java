package lithium.service.domain;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.tokens.EnableTokenUtilServices;
import lithium.util.SecurityKeyPairGenerator;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@LithiumService
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableLeaderCandidate
@EnableTokenUtilServices
public class ServiceDomainApplication extends LithiumServiceApplication {
	@Autowired
	private ServiceDomainInit initStuff;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceDomainApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		if (isLoadTestData()) {
			initStuff.init();
		} else {
			initStuff.initProviderTypes();
      initStuff.initEcosystemRelationshipTypes();
		}
	}

	@Bean
	public SecurityKeyPairGenerator securityKeyPairGenerator() {
		return new SecurityKeyPairGenerator();
	}
	
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
