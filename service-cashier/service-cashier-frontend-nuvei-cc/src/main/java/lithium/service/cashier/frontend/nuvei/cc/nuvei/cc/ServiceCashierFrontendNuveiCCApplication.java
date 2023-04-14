package lithium.service.cashier.processor.nuvei.cc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableCashierInternalClientService
@EnableUserApiInternalClientService
@EnableRestTemplate
@EnableConfigurationProperties
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierFrontendNuveiCCApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCashierFrontendNuveiCCApplication.class, args);
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
        return builder.build();
    }

}
