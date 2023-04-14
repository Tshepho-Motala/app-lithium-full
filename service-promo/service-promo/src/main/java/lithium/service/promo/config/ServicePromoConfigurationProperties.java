package lithium.service.promo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.promo")
@Data
public class ServicePromoConfigurationProperties {
    private String externalSecretKey;
    private String defaultTimezone;
}