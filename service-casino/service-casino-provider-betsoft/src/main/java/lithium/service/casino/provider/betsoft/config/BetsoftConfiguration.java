package lithium.service.casino.provider.betsoft.config;

import lithium.service.casino.provider.betsoft.LoggingRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class BetsoftConfiguration {
	
	@Bean(name="lithium.service.casino.provider.betsoft.resttemplate")
	@Primary
	public RestTemplate getRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
//		factory.setConnectTimeout(restTemplateConnectTimeoutMs);
//		factory.setReadTimeout(restTemplateReadTimeoutMs);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		interceptors.add(new BetsoftXmlInterceptor());
		restTemplate.setInterceptors(interceptors);
		log.info("MessageConverters :: "+restTemplate.getMessageConverters());
		return restTemplate;
	}

	public class BetsoftXmlInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			ClientHttpResponse response = execution.execute(request, body);
			HttpHeaders headers = response.getHeaders();
			if (headers.containsKey("Content-Type")) {
				headers.remove("Content-Type");
			}
			headers.add("Content-Type", "application/xml");
			return response;
		}
	}
}
