package lithium.service.cashier.processor.upay.upay;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;

@LithiumService
@EnableAsync
@EnableLithiumServiceClients
@EnableLocaleContextProcessor
@EnableDomainClient
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
	
	
}
