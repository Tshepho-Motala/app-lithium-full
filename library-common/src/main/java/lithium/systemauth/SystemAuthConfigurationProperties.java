package lithium.systemauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="lithium.system.auth")
@Data
public class SystemAuthConfigurationProperties {

	private String tokenUrl;
	private String username;
	private String password;
	private Boolean standalone;
	
}
