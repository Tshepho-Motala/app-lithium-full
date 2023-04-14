package lithium.service.pushmsg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "lithium.services.pushmsg")
public class ServicePushMsgConfigurationProperties {
}