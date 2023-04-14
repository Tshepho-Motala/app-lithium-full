package lithium.service.cashier.mock.btc.globalbitlocker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.btc.globalbitlocker")
@Data
public class Configuration {
	private String apiKey;
}