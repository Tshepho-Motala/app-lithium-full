package lithium.service.cashier.mock.wumg.paymentclicks;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(Configuration.class)
public class ServiceCashierMockWUMGPaymentClicksApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockWUMGPaymentClicksApplication.class, args);
	}
}
