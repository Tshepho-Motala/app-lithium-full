package lithium.service.cashier.mock.cc.trustspay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableDiscoveryClient
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(Configuration.class)
public class ServiceCashierMockCCTrustsPayApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceCashierMockCCTrustsPayApplication.class, args);
	}
}