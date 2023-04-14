package lithium.service.cashier.processor.trustly;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierTrustlyApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierTrustlyApplication.class, args);
	}
}
