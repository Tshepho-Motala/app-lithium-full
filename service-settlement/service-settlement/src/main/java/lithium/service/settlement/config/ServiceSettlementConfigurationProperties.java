package lithium.service.settlement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.settlement")
@Data
public class ServiceSettlementConfigurationProperties {
}