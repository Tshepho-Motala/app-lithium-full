package lithium.service.sms;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.access.client.EnableAccessService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableAccessService
@EnableLeaderCandidate
@EnableScheduling
public class ServiceSMSApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceSMSApplication.class, args);
	}
}
