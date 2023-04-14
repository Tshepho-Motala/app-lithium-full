package lithium.letsencrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="letsencrypt")
@Data
public class LetsEncryptConfigurationProperties {

	private String request = "HASHREQUEST";
	private String response = "HASHRESPONSE";
}
