package lithium.service.changelog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

// The default may not be specified here as it will be used in the @Scheduled annotation that will run
// before this bean is instantiated. Moved to application.yml

@ConfigurationProperties(prefix = "lithium.service.changelog")
@Data
public class ChangeLogConfigurationProperties {
	
}
