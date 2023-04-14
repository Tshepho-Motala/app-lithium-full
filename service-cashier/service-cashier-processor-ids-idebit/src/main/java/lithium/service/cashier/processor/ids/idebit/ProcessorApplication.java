package lithium.service.cashier.processor.ids.idebit;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.services.LithiumService;
import lithium.application.LithiumShutdownSpringApplication;

@LithiumService
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
}
