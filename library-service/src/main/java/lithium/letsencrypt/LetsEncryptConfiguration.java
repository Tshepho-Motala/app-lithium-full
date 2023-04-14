package lithium.letsencrypt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableConfigurationProperties(value=LetsEncryptConfigurationProperties.class)
public class LetsEncryptConfiguration {

	public LetsEncryptConfiguration() {
	}
	
}
