package lithium.cashier;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="lithium.service.cashier.provider")
public class ProviderConfiguration {
	private String name;
	private String url;
	private String domainName;
	private String type;
	private Map<String, String> properties = new HashMap<String, String>();
}
