package service.casino.provider.cataboom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class APIAuthentication {

	private String providerUrl;
	private String domainName;

	private BrandsConfigurationBrand brandConfiguration;
	
	public String getProviderGuid() {
		return domainName+"/"+providerUrl;
	}
}
