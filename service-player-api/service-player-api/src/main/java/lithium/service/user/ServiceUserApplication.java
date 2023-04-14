package lithium.service.user;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
public class ServiceUserApplication extends LithiumServiceApplication {
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserApplication.class, args);
	}
	
}
