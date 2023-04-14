package lithium.service.machine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableTranslationsStream
@EnableMailStream
public class ServiceMachineApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceMachineApplication.class, args);
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@Bean(name="lithium.service.machine.RestTemplate")
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));

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
}
