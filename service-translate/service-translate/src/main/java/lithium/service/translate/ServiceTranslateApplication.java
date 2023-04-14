package lithium.service.translate;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLeaderCandidate
@EnableChangeLogService
@EnableLocaleContextProcessor
@EnableDomainClient
@EnableScheduling
public class ServiceTranslateApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceTranslateApplication.class, args);
	}
}
