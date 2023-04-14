package lithium.service.cashier.processor.neteller;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;

@EnableAsync
@LithiumService
@EnableLithiumServiceClients
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
}
