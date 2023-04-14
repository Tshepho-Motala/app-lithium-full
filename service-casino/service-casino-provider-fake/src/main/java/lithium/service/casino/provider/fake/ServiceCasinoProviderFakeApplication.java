package lithium.service.casino.provider.fake;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceCasinoProviderFakeApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderFakeApplication.class, args);
	}
}
