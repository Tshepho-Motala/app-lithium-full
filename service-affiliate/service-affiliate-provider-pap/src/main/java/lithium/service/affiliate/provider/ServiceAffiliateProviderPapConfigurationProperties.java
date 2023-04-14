package lithium.service.affiliate.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.services.affiliate")
@Data
public class ServiceAffiliateProviderPapConfigurationProperties {
	
	private String publicUrl;

}
