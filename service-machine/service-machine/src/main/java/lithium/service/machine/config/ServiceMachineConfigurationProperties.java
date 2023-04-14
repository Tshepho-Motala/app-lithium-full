package lithium.service.machine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.machine")
@Data
public class ServiceMachineConfigurationProperties {
	
	Long statRate;
	String graphiteBaseUrl;
	
}