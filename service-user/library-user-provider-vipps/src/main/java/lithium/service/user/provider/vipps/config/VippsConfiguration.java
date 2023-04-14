package lithium.service.user.provider.vipps.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.rest.LoggingRequestInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class VippsConfiguration {
	
	@Bean(name = "lithium.service.user.provider.vipps.resttemplate")
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
//		factory.setConnectTimeout(restTemplateConnectTimeoutMs);
//		factory.setReadTimeout(restTemplateReadTimeoutMs);
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				if ((response!=null) && (!response.getStatusCode().is2xxSuccessful())) log.error("hasError :: StatusCode:"+response.getStatusCode()+" -- StatusText: "+response.getStatusText());
				return false; // or whatever you consider an error
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if ((response!=null) && (!response.getStatusCode().is2xxSuccessful())) log.error("handleError :: StatusCode:"+response.getStatusCode()+" -- StatusText: "+response.getStatusText());
			}
		});
		log.info("restTemplate :"+restTemplate);
		return restTemplate;
	}
}
