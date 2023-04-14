package lithium.service.cashier.processor.opay;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableCashierInternalClientService
@EnableUserApiInternalClientService
@EnableAccountingClientService
@EnableDomainClient
@EnableLimitInternalSystemClient
@EnableCustomHttpErrorCodeExceptions
@EnableRestTemplate
public class ServiceCashierOpayApplication extends LithiumServiceProcessorApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCashierOpayApplication.class, args);
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
