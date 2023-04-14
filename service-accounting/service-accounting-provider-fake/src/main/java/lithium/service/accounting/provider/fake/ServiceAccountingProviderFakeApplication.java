package lithium.service.accounting.provider.fake;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceAccountingProviderFakeApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccountingProviderFakeApplication.class, args);
	}
}
