package lithium.service.casino.provider.sportsbook.config;

import lombok.Getter;

public enum ProviderConfigProperties {
	HASH_PASSWORD ("hashPassword"),
	EXTERNAL_TRANSACTION_INFO_URL("externalTransactionInfoUrl"),
	PLAYER_OFFSET("playerOffset"),
	BETSEARCH_URL("betSearchUrl"),
	BETSEARCH_KEY("betSearchKey"),
	BETSEARCH_BRAND("betSearchBrand"),
	SPORTS_FREE_BETS_URL("sportsFreeBetsUrl"),
	BONUS_RESTRICTION_URL("bonusRestrictionUrl"),
	BONUS_RESTRICTION_KEY("bonusRestrictionKey");

	@Getter
	private final String value;
		
	ProviderConfigProperties(String valueParam) {
			value = valueParam;
		}
}
