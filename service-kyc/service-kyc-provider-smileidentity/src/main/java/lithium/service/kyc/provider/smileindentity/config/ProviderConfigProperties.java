package lithium.service.kyc.provider.smileindentity.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	VERIFY_API_URL("verifyApiUrl"),
	PARTNER_ID("partnerId"),
	API_KEY("apiKey"),
	BANK_LIST("bankListUrl"),
	COUNTRY("country"),
	CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
	CONNECT_TIMEOUT("connectTimeout"),
	SOCKET_TIMEOUT("socketTimeout");
	@Getter
	private final String value;

	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}
