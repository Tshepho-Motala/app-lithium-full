package lithium.service.role.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
public class RolesConfiguration {
	@Bean
	public RolesService rolesService() {
		return new RolesService();
	}
}
