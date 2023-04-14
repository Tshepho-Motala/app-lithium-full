package lithium.service.cashier.provider.mercadonet.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIAuthentication {

	private String apiKey;
	private String providerName;
	private String domainName;
	private BrandsConfigurationBrand brandConfiguration;
	
	public String getProviderGuid() {
		return domainName+"/"+providerName;
	}
}
