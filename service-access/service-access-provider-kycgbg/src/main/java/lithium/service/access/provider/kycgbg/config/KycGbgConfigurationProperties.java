package lithium.service.access.provider.kycgbg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lithium.services.access.kycgbg")
public class KycGbgConfigurationProperties {
	private Integer readTimeout;
	private Integer connectionTimeout;
}
