package lithium.service.mail;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.access.client.EnableAccessService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableJpaRepositories
@EnableDomainClient
@EnableProviderClient
@EnableLithiumMetrics
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableLeaderCandidate
@EnableAccessService
@EnableUserApiInternalClientService
@EnableScheduling
public class ServiceMailApplication extends LithiumServiceApplication {

	@Autowired
	ServiceMailInit serviceMailInit;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceMailApplication.class, args);
	}
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		serviceMailInit.initProviderTypes();
	}
}
