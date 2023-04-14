package lithium.service.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.services.affiliate")
@Data
public class ServiceEventConfigurationProperties {
	
	private String publicUrl;

}
