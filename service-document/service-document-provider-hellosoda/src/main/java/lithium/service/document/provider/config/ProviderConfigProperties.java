package lithium.service.document.provider.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	PROFILE_API_V1_URL ("profileApiV1Url"),
	PROFILE_API_URL ("profileApiUrl"),
	I_DOCUFY_API_URL("iDocufyApiUrl"),
	PROFILE_BEARER ("profileBearer"),
	PRODUCT_ID ("productId");

	@Getter
	private final String value;
		
	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}
