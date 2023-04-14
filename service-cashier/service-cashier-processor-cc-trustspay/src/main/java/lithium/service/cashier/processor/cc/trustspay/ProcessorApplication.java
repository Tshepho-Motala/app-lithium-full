package lithium.service.cashier.processor.cc.trustspay;

import org.springframework.boot.SpringApplication;

import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.processor.cc.trustspay.ws.EnableQueryClient;
import lithium.services.LithiumService;

@LithiumService
@EnableQueryClient
public class ProcessorApplication extends LithiumServiceProcessorApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProcessorApplication.class, args);
	}
	
}
