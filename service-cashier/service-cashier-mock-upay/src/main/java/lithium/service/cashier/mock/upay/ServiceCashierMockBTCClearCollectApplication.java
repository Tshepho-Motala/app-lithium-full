package lithium.service.cashier.mock.upay;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(Configuration.class)
@EnableLocaleContextProcessor
@EnableDomainClient
public class ServiceCashierMockBTCClearCollectApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockBTCClearCollectApplication.class, args);
	}
}
