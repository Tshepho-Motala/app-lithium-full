package lithium.service.cashier.processor.flutterwave;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@LithiumService
@EnableAsync
@EnableLithiumServiceClients
@EnableRestTemplate
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierFlutterwaveApplication extends LithiumServiceProcessorApplication {
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierFlutterwaveApplication.class, args);
	}

	@Bean()
	public CacheManager banksCacheManager() {
		return new ConcurrentMapCacheManager("lithium.service.cashier.processor.flutterwave.banks") {
			@Override
			protected Cache createConcurrentMapCache(String name) {
				return new ConcurrentMapCache(
						name,
						CacheBuilder.newBuilder()
								.expireAfterWrite(12, TimeUnit.HOURS)
								.build().asMap(),
						false);
			}
		};
	}

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	@Bean
	public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
		return builder.build();
	}
}
