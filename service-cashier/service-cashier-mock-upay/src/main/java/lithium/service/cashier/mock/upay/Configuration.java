package lithium.service.cashier.mock.upay;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.upay")
@Data
public class Configuration {
	private String apiSecret;
}
