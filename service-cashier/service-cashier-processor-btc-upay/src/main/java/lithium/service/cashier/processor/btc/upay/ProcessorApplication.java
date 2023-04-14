package lithium.service.cashier.processor.btc.upay;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;

@LithiumService
@EnableAsync
@EnableLithiumServiceClients
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
	
	
}
