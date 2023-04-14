package lithium.service.cashier.mock.btc.clearcollect;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.btc.clearcollect")
@Data
public class Configuration {
//	private String apiKey;
	private String apiSecret;
	private String responseUrl;
}
