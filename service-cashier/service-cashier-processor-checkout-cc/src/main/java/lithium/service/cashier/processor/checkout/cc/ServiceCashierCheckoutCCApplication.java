package lithium.service.cashier.processor.checkout.cc;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierCheckoutCCApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierCheckoutCCApplication.class, args);
	}
}
