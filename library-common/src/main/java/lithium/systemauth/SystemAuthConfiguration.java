package lithium.systemauth;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SystemAuthConfigurationProperties.class)
public class SystemAuthConfiguration {

	@Bean SystemAuthService systemAuthService() {
		return new SystemAuthService();
	}
	
}
