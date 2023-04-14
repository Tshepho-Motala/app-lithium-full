package lithium.service.casino.mock.twowinpower;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(Configuration.class)
public class ServiceCasinoMockTwoWinPowerApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoMockTwoWinPowerApplication.class, args);
	}
	
	@Bean(name="lithium.service.casino.mock.twowinpower.resttemplate")
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
