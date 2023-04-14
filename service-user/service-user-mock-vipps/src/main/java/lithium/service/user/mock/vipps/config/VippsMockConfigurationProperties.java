package lithium.service.user.mock.vipps.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ConfigurationProperties(prefix = "lithium.vipps.mock")
public class VippsMockConfigurationProperties {
	private String url;
	private String accessTokenClientId;
	private String accessTokenClientSecret;
	private String ocpApimSubscriptionKey;
}