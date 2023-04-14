package lithium.service.raf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.raf")
@Data
public class ServiceRAFConfigurationProperties {
}