package lithium.service.casino.provider.incentive.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	HASH_PASSWORD ("hashPassword");

	@Getter
	private final String value;
		
	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}