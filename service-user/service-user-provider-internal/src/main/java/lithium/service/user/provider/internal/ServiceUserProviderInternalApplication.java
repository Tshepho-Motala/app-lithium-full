package lithium.service.user.provider.internal;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
public class ServiceUserProviderInternalApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderInternalApplication.class, args);
	}
	
}
