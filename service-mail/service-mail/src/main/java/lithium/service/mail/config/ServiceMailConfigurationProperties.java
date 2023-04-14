package lithium.service.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.mail")
@Data
public class ServiceMailConfigurationProperties {
	private String processingJobCron;
	private Integer processingJobPageSize;
	private int errorThreshold = 10;
	private Integer maxProcessingMins = 60;
}