package lithium.service.cashier.mock.neteller;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "lithium.service.cashier.mock.neteller")
public class EnabledConfigurationProperties {
	private String accessTokenClientSecret;
}