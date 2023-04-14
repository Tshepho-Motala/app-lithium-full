package lithium.service.cashier.processor.paynl;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.processor.paynl.handler.RestTemplateResponseErrorHandler;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
public class ServiceCashierPaynlApplication extends LithiumServiceProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCashierPaynlApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.errorHandler(new RestTemplateResponseErrorHandler()).build();
    }

}
