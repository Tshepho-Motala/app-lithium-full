package lithium.service.cashier.mock.inpay;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.mock.inpay.converters.ObjectToUrlEncodedConverter;
import lithium.service.cashier.processor.inpay.services.InpayCryptoService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableRestTemplate
public class ServiceCashierMockInpayApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCashierMockInpayApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
        RestTemplate template = builder.build();
        ObjectMapper mapper = new ObjectMapper();
        template.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));
        return template;
    }

    @Bean
    public InpayCryptoService cryptoService(){
        return new InpayCryptoService();
    }

    @Bean(name="lithium.service.cashier.service-cashier-mock-inpay.taskScheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "inpay_mock_scheduler");
        return threadPoolTaskScheduler;
    }

}
