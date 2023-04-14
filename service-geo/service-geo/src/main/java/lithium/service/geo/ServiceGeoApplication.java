package lithium.service.geo;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.stream.transactionlabel.EnableTransactionLabelStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.geo.config.ServiceGeoConfigurationProperties;
import lithium.service.geo.services.MaxMindSynchronizer;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableConfigurationProperties(ServiceGeoConfigurationProperties.class)
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableTransactionLabelStream
@EnableLocaleContextProcessor
@EnableDomainClient
@EnableScheduling
public class ServiceGeoApplication extends LithiumServiceApplication {
	@Autowired private MaxMindSynchronizer maxMindSynchronizer;

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);

		maxMindSynchronizer.download();
	}


	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceGeoApplication.class, args);
	}
}
