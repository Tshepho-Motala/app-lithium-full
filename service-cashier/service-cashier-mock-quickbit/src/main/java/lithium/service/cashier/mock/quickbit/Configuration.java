package lithium.service.cashier.mock.quickbit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="lithium.service.cashier.mock.quickbit")
@Data
public class Configuration {
	private String secretKey;
}