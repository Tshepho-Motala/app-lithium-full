package lithium.service.casino.provider.rival;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
public class ServiceCasinoProviderRivalApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderRivalApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
