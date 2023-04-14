package lithium.service.casino.provider.sgs.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIAuthentication {

	private String apiKey;
	private String providerUrl;
	private String domainName;
	private BrandsConfigurationBrand brandConfiguration;
	
	public String getProviderGuid() {
		return domainName+"/"+providerUrl;
	}
}
