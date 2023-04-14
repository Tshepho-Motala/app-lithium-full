package lithium.service.cashier.mock.cc.qwipi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.cc.qwipi")
@Data
public class Configuration {
	private String md5Key;
}
