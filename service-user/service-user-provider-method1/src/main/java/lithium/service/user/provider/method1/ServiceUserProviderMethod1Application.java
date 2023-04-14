package lithium.service.user.provider.method1;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
public class ServiceUserProviderMethod1Application extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderMethod1Application.class, args);
	}
	
}
