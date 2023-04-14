package lithium.service.pushmsg;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.client.changelog.EnableChangeLogService;
import lithium.service.access.client.EnableAccessService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@EnableAsync
@LithiumService
@EnableAccessService
@EnableChangeLogService
@EnableLithiumServiceClients
public class ServicePushMsgApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServicePushMsgApplication.class, args);
	}
}
