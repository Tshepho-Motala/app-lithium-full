//package lithium.service.access.provider.iovation.controllers;
//
//import lithium.application.LithiumShutdownSpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.cloud.netflix.feign.LithiumFeignClientsRegistrar;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.client.RestTemplate;
//
//import lithium.client.changelog.EnableChangeLogService;
//import lithium.service.access.provider.iovation.services.AccessProviderIovationService;
//import lithium.service.access.provider.iovation.services.AddEvidenceService;
//import lithium.service.access.provider.iovation.services.CheckTransactionDetailsService;
//import lithium.service.access.provider.iovation.services.RetractEvidenceService;
//import lithium.service.client.EnableLithiumServiceClients;
//import lithium.service.client.LithiumServiceClientFactory;
//
//@TestConfiguration
//@SpringBootApplication
//public class TestApplication {
//	public static void main(String[] args) {
//		LithiumShutdownSpringApplication.run(TestApplication.class, args);
//	}
//
//	@Bean
//	public AddEvidenceService addEvidenceService() {
//		return new AddEvidenceService();
//	}
//
//	@Bean
//	public RetractEvidenceService retractEvidenceService() {
//		return new RetractEvidenceService();
//	}
//
//	@Bean
//	public AccessProviderIovationService accessProviderIovationService() {
//		return new AccessProviderIovationService();
//	}
//
//	@Bean
//	public RestTemplate restTemplate() {
//		return new RestTemplate();
//	}
//
//	@Bean
//	public CheckTransactionDetailsService checkTransactionDetailsService() {
//		return new CheckTransactionDetailsService();
//	}
//
//	@Bean
//	public LithiumServiceClientFactory lithiumServiceClientFactory() {
//		return new LithiumServiceClientFactory();
//	}
//
//	@Bean
//	public LithiumFeignClientsRegistrar lithiumFeignClientsRegistrar() {
//		return new LithiumFeignClientsRegistrar();
//	}
//}
