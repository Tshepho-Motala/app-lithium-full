package lithium.service.cashier.mock.btc.globalbitlocker;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@EnableAsync
@EnableRetry
@LithiumService
@EnableConfigurationProperties(Configuration.class)
public class ServiceCashierMockBTCGlobalBitLockerApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockBTCGlobalBitLockerApplication.class, args);
	}
}
