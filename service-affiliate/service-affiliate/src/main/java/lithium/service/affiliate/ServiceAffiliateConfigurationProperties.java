package lithium.service.affiliate;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.services.affiliate")
@Data
public class ServiceAffiliateConfigurationProperties {
	
	private String publicUrl;

}
