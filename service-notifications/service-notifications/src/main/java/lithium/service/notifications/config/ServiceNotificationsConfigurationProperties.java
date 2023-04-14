package lithium.service.notifications.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.notifications")
@Data
public class ServiceNotificationsConfigurationProperties {
	private int minsBeforeProcessingNotificationChannels = 1;
}