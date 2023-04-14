package lithium.service.entity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.entity")
@Data
public class ServiceEntityConfigurationProperties {
}