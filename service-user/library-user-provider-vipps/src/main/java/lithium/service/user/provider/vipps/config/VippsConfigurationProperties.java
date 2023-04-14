package lithium.service.user.provider.vipps.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ConfigurationProperties(prefix = "lithium.vipps")
public class VippsConfigurationProperties {
	private String apiToken;
	private List<String> allowedIPs;
}