package lithium.service.casino.provider.rival.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class APIAuthentication {

	private String apiKey;
	private String providerUrl;
	private String domainName;
	private BrandsConfigurationBrand brandConfiguration;
	
	public String getProviderGuid() {
		return domainName+"/"+providerUrl;
	}
}
