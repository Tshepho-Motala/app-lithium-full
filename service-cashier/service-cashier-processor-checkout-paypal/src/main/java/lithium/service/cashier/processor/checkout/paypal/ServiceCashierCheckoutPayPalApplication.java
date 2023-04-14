package lithium.service.cashier.processor.checkout.paypal;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@LithiumService
@EnableLithiumServiceClients
public class ServiceCashierCheckoutPayPalApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierCheckoutPayPalApplication.class, args);
	}
}
