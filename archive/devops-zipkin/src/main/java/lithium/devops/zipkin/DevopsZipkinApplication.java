package lithium.devops.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

@SpringBootApplication
@EnableZipkinStreamServer
public class DevopsZipkinApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsZipkinApplication.class, args);
	}
}
