package lithium.service.casino;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceCasinoApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoApplication.class, args);
	}
}
