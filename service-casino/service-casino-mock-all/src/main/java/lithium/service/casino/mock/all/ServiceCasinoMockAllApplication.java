package lithium.service.casino.mock.all;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.accounting.client.stream.transactionlabel.EnableTransactionLabelStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableLithiumServiceClients
@EnableTransactionLabelStream
public class ServiceCasinoMockAllApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoMockAllApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
