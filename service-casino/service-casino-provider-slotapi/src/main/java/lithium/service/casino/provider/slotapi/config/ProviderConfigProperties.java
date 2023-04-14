package lithium.service.casino.provider.slotapi.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	HASH_PASSWORD ("hashPassword"),
	BET_HISTORY_ROUND_DETAIL_URL("betHistoryRoundDetailUrl"),
	BET_HISTORY_ROUND_DETAIL_PROVIDER_ID("betHistoryRoundDetailPid");


	@Getter
	private final String value;
		
	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}