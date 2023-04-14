package lithium.service.user.mock.vipps;

import java.util.ArrayList;
import java.util.List;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lithium.rest.LoggingRequestInterceptor;
import lithium.service.user.mock.vipps.config.VippsMockConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(VippsMockConfigurationProperties.class)
public class VippsMockApplication { // extends LithiumServiceApplication {
	
	@Bean(name = "lithium.service.user.mock.vipps.resttemplate")
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
//		factory.setConnectTimeout(restTemplateConnectTimeoutMs);
//		factory.setReadTimeout(restTemplateReadTimeoutMs);
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		log.info("restTemplate :"+restTemplate);
		return restTemplate;
	}
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(VippsMockApplication.class, args);
	}
}
