package lithium.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class LithiumRestTemplateCustomizer implements RestTemplateCustomizer {
	@Autowired private LithiumRestConfigurationProperties properties;

	@Override
	public void customize(RestTemplate restTemplate) {
		BufferingClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory() {
					@Override
					public void setOutputStreaming(boolean outputStreaming) {
						super.setOutputStreaming(false);
					}
				});

		restTemplate.getInterceptors().add(new LoggingRequestInterceptor(properties));
		restTemplate.setRequestFactory(factory);
		restTemplate.getMessageConverters().stream()
				.filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
				.findFirst()
				.ifPresent(converter -> {
					((MappingJackson2HttpMessageConverter)converter).setObjectMapper(new ObjectMapper());
					((MappingJackson2HttpMessageConverter)converter).setSupportedMediaTypes(getAllowedMediaTypes());
				});
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				//FIXME: Add some logic in here to handle the decision on wether it is an error or not
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				//FIXME: There will be no error, so no handling... for now.
				// We need to do some work to inject allowed error codes from exceptions
			}
		});
	}

	protected ArrayList<MediaType> getAllowedMediaTypes() {
		ArrayList<MediaType> list = new ArrayList<>();
		list.add(new MediaType("application", "json"));
		list.add(new MediaType("text", "javascript"));
		list.add(new MediaType("text", "html"));
		return list;
	}
}
