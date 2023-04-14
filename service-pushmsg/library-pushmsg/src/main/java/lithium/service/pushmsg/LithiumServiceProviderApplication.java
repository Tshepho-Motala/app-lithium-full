package lithium.service.pushmsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.rest.LoggingRequestInterceptor;
import lithium.service.pushmsg.client.objects.Provider;
import lithium.service.pushmsg.provider.stream.EnableProvidersStream;
import lithium.service.pushmsg.provider.stream.ProvidersStream;
import lithium.services.LithiumServiceApplication;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan
@EnableProvidersStream
public abstract class LithiumServiceProviderApplication extends LithiumServiceApplication {
	@Autowired ProvidersStream providersStream;
	@Autowired ProviderFileReader providerFileReader;
	
	@Bean(name="lithium.service.pushmsg.RestTemplate")
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		
		restTemplate.setInterceptors(interceptors);
		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
		
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
				jsonConverter.setObjectMapper(new ObjectMapper());
				ArrayList<MediaType> list = new ArrayList<>();
				list.add(new MediaType("application", "json"));
				list.add(new MediaType("text", "javascript"));
				list.add(new MediaType("text", "html"));
				jsonConverter.setSupportedMediaTypes(list);
			}
		}
		
		return restTemplate;
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		try {
			Map<String, Provider> providers = providerFileReader.read();
			for (Provider provider : providers.values()) {
				log.info("Provider " + provider.getName());
				log.debug("Provider " + provider.getName() + " json: \n" + JsonStringify.objectToString(provider));
				providersStream.registerProvider(provider);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
}