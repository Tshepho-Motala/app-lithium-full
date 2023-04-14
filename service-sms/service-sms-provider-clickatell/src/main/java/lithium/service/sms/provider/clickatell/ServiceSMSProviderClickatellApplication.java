package lithium.service.sms.provider.clickatell;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.sms.LithiumServiceProviderApplication;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServiceSMSProviderClickatellApplication extends LithiumServiceProviderApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceSMSProviderClickatellApplication.class, args);
	}
}
