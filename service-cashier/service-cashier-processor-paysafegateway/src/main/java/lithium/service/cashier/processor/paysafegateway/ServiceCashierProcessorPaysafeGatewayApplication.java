package lithium.service.cashier.processor.paysafegateway;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServiceCashierProcessorPaysafeGatewayApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierProcessorPaysafeGatewayApplication.class, args);
	}
}
