package lithium.service.access.provider.gamstop.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class APIAuthentication {
	private String providerUrl;
	private String domainName;
	private BrandsConfigurationBrand brandConfiguration;
	
	public String getProviderGuid() {
		return domainName+"/"+providerUrl;
	}
}
