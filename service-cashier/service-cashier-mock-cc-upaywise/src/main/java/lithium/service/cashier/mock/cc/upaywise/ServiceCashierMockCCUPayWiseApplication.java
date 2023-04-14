package lithium.service.cashier.mock.cc.upaywise;

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
public class ServiceCashierMockCCUPayWiseApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockCCUPayWiseApplication.class, args);
	}
}
