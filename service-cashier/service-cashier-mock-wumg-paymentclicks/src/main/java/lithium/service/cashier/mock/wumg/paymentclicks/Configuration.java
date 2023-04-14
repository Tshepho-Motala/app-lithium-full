package lithium.service.cashier.mock.wumg.paymentclicks;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.wumg.paymentclicks")
@Data
public class Configuration {
	private String apiUser;
	private String apiPassword;
}
