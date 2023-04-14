package lithium.service.xp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.xp")
@Data
public class ServiceXPConfigurationProperties {
}