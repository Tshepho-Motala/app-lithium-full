package lithium.service.cashier.mock.hexopay;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.rest.EnableRestTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableAsync
@EnableRetry
@EnableRestTemplate
@EnableScheduling
@EnableConfigurationProperties(Configuration.class)
public class ServiceCashierMockHexopayApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierMockHexopayApplication.class, args);
	}
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
	@Bean
	public javax.validation.Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean(name="lithium.service.cashier.service-cashier-mock-hexopay.taskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
		ThreadPoolTaskScheduler threadPoolTaskScheduler
				= new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix(
				"hexopay_mock_scheduler");
		return threadPoolTaskScheduler;
	}
}
