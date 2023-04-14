package lithium.service.mail.provider.smtp;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.mail.LithiumServiceProviderApplication;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServiceMailProviderSMTPApplication extends LithiumServiceProviderApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceMailProviderSMTPApplication.class, args);
	}
}
