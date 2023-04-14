package lithium.service.casino.mock.twowinpower;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "lithium.service.casino.mock.twowinpower")
public class Configuration {
	private String apiKey;
	private String endpointUrl;
	private String merchantId;
	private String merchantKey;
}