package lithium.devops.hystrixdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

@EnableTurbineStream
@SpringBootApplication
@EnableHystrixDashboard
public class DevopsHystrixdashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsHystrixdashboardApplication.class, args);
	}
}
