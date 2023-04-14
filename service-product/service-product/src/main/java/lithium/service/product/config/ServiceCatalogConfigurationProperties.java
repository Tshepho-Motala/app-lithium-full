package lithium.service.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "lithium.services.catalog")
public class ServiceCatalogConfigurationProperties {
}