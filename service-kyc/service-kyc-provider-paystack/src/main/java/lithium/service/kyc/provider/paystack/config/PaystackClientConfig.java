package lithium.service.kyc.provider.paystack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaystackClientConfig {
	@Autowired
	private PaystackConfigurationProperties properties;

}
