package lithium.service.access.provider.iovation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "lithium.services.access.iovation")
public class IovationConfigurationProperties {
	private Integer connectTimeout;
	private Integer connectionRequestTimeout;
	private Integer socketTimeout;
}
