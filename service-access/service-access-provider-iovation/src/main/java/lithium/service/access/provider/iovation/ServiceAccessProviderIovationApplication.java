package lithium.service.access.provider.iovation;

import java.util.ArrayList;
import java.util.List;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import lithium.client.changelog.EnableChangeLogService;
import lithium.rest.LoggingRequestInterceptor;
import lithium.service.access.provider.iovation.config.IovationConfigurationProperties;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableChangeLogService
@EnableLithiumServiceClients
@EnableConfigurationProperties(IovationConfigurationProperties.class)
public class ServiceAccessProviderIovationApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccessProviderIovationApplication.class, args);
	}

	@Bean(name="lithium.service.access.povider.iovation.RestTemplate")
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		
		return restTemplate;
	}
}
