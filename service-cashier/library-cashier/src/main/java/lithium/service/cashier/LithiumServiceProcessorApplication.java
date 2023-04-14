package lithium.service.cashier;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.objects.transaction.dto.Method;
import lithium.service.cashier.client.objects.transaction.dto.Processor;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.cashier.processor.http.LoggingRequestInterceptor;
import lithium.service.cashier.stream.EnableProcessorsStream;
import lithium.service.cashier.stream.ProcessorsStream;
import lithium.services.LithiumServiceApplication;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableProcessorsStream
// @EnableConfigurationProperties({ ProcessorConfiguration.class,
// MethodsConfiguration.class })
@EnableCashierInternalClientService
@ComponentScan
public abstract class LithiumServiceProcessorApplication extends LithiumServiceApplication {
	
	@Value("${lithium.service.cashier.rest-template-connect-timeout-in-milliseconds:60000}")
	private int restTemplateConnectTimeoutMs;

	@Value("${lithium.service.cashier.rest-template-read-timeout-in-milliseconds:60000}")
	private int restTemplateReadTimeoutMs;
	
	@Autowired
	private ProcessorsStream processorsStream;

	@Autowired
	private MethodFileReader methodFileReader;
	@Autowired
	private ProcessorFileReader processorFileReader;

	@Bean(name="lithium.service.cashier.RestTemplate")
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
		factory.setConnectTimeout(restTemplateConnectTimeoutMs);
		factory.setReadTimeout(restTemplateReadTimeoutMs);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

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

		if (processorsStream == null) {
			log.error(
					"Processor stream is null. Not sure why this would be a valid case, but the old code was ok with it so carrying on...");
			return;
		}

		try {
			Map<String, Method> methods = methodFileReader.read();
			for (Method method : methods.values()) {
				log.info("Method " + method.getName());
				log.debug("Method " + method.getName() + " json: \n" + JsonStringify.objectToString(method));

				ClassPathResource cpr = new ClassPathResource(method.getImage().getFilename());
				InputStream is = cpr.getInputStream();
				if (method.getImage().getFiletype() == null) {
					String contentType = URLConnection.guessContentTypeFromStream(is);
					if (contentType == null) {
						String fn = method.getImage().getFilename();
						contentType = "image/"+fn.substring(fn.lastIndexOf(".")+1, fn.length());
					}
					method.getImage().setFiletype(contentType);
				}
				method.getImage().setBase64(FileCopyUtils.copyToByteArray(is));
				method.getImage().setFilesize(new Long(method.getImage().getBase64().length));
				log.info("Method image size " + method.getImage().getBase64().length);
			}

			Map<String, Processor> processors = processorFileReader.read();
			for (Processor processor : processors.values()) {
				log.info("Processor " + processor.getName());
				log.debug("Processor " + processor.getName() + " json: \n" + JsonStringify.objectToString(processor));
				processor.setMethods(new ArrayList<>());
				for (String methodName : processor.getMethodNames()) {
					Method method = methods.get(methodName);
					if (method == null) {
						throw new Exception("Processor " + processor.getName() + " references a method " + methodName
								+ " that does not exist locally.");
					}
					processor.getMethods().add(method);
				}
				processorsStream.registerProcessor(processor);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
}
