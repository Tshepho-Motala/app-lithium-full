package lithium.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "lithium.rest.logging")
@Data
public class LithiumRestConfigurationProperties {
	private List<String> obfuscateFieldsRequest;
	private List<String> obfuscateFieldsResponse;
}
