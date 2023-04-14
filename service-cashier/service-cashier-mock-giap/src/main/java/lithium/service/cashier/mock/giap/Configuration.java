package lithium.service.cashier.mock.giap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "lithium.service.cashier.mock.cc.giap")
public class Configuration {
	private String key;
}
