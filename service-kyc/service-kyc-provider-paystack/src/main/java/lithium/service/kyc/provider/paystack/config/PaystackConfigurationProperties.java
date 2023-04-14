package lithium.service.kyc.provider.paystack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lithium.services.kyc.paystack")
public class PaystackConfigurationProperties {
	private Integer readTimeout;
	private Integer connectionTimeout;
}
