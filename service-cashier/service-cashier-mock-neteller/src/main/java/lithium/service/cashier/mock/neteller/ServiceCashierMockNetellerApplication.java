package lithium.service.cashier.mock.neteller;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableConfigurationProperties(EnabledConfigurationProperties.class)
public class ServiceCashierMockNetellerApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockNetellerApplication.class, args);
	}
}
