package lithium.service.cashier.mock.paynl;

import lithium.rest.EnableRestTemplate;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableRestTemplate
@EnableConfigurationProperties(Configuration.class)
public class ServiceCashierMockPaynlApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCashierMockPaynlApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean(name="lithium.service.cashier.service-cashier-mock-paynl.taskScheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "paynl_mock_scheduler");
        return threadPoolTaskScheduler;
    }
    
}
