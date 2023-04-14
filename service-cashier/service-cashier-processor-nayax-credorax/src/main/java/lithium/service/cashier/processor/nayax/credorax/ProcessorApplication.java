package lithium.service.cashier.processor.nayax.credorax;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.services.LithiumService;

@LithiumService
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ProcessorApplication.class, args);
	}
}
