package lithium.service.access.provider.sphonic.iban.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.services.access.sphonic.iban")
@Configuration
public class ServiceAccessProviderSphonicIBANConfigurationProperties {
}
