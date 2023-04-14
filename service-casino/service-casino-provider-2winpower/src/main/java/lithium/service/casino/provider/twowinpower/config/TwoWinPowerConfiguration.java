package lithium.service.casino.provider.twowinpower.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.twowinpower.LoggingRequestInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TwoWinPowerConfiguration {
	
	@Bean(name="lithium.service.casino.provider.twowinpower.resttemplate")
	public RestTemplate getRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
//		factory.setConnectTimeout(restTemplateConnectTimeoutMs);
//		factory.setReadTimeout(restTemplateReadTimeoutMs);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		log.info("MessageConverters :: "+restTemplate.getMessageConverters());
		return restTemplate;
	}
}
