package lithium.service.cashier.processor.smartcash;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.application.LithiumShutdownSpringApplication;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


@LithiumService
@EnableCashierInternalClientService
@EnableUserApiInternalClientService
@EnableDomainClient
@EnableConfigurationProperties
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierSmartcashApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierSmartcashApplication.class, args);
	}
	@Bean
	public ObjectMapper mapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.errorHandler(new DefaultResponseErrorHandler()).build();
	}
}
