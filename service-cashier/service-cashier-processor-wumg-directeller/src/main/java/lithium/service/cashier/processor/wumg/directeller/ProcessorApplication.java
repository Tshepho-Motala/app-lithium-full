package lithium.service.cashier.processor.wumg.directeller;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.processor.wumg.directeller.ws.EnableTransactClient;
import lithium.services.LithiumService;

@LithiumService
@EnableTransactClient
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
}
