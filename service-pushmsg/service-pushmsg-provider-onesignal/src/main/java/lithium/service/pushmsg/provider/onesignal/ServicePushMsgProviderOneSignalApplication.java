package lithium.service.pushmsg.provider.onesignal;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.pushmsg.LithiumServiceProviderApplication;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServicePushMsgProviderOneSignalApplication extends LithiumServiceProviderApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServicePushMsgProviderOneSignalApplication.class, args);
	}
}
