package lithium.service.sms.provider.mvend;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.sms.LithiumServiceProviderApplication;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServiceSMSProviderMvendApplication extends LithiumServiceProviderApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceSMSProviderMvendApplication.class, args);
	}
}
