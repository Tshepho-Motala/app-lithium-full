package lithium.service.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.services.sms")
@Data
@Component
public class ServiceSMSConfigurationProperties {
	private String processingJobCron;
	private Integer processingJobPageSize = 10;
	private Integer smsErrorCountLessThan = 1;
	private Integer maxProcessingMins = 60;
}