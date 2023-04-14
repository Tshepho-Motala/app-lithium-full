package lithium.service.cashier.processor.bluem.ideal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierBluemIdealApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierBluemIdealApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
