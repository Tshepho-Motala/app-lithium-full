package lithium.service.kyc.provider.onfido.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	BASE_API_URL("baseUrl"),
	API_TOKEN("apiToken"),
	REPORT_NAMES("reportNames"),
	WEBHOOK_IDS("webhookIds"),
	MATCH_DOCUMENT_ADDRESS("matchDocumentAddress"),
	MATCH_FIRST_NAME("matchFirstName"),
	SUPPORTED_ISSUING_COUNTRIES("supportedIssuingCountries");
	@Getter
	private final String value;

	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}
