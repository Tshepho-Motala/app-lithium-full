package lithium.service.cashier.frontend.paysafegateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="lithium.service.cashier.frontend.paysafegateway")
@Data
public class Configuration {
	private String tokenPostUrl;
}