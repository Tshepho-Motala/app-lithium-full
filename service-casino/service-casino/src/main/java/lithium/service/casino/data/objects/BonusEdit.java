package lithium.service.casino.data.objects;

import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.GameCategory;
import lithium.service.casino.data.enums.Volatility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BonusEdit {
	private Map<String, Boolean> activeDays;
	private Map<String, String> activeTime;
	private String activeTimezone;
	private String bonusCode;
	private String bonusName;
	private String bonusDescription;
	private List<FreespinRules> freespinRules;
	private List<CasinoChipRules> casinoChipRules;
	private List<InstantRewardRules> instantRewardRules;
	private List<InstantRewardFreespinRules> instantRewardFreespinRules;
	private List<GameCategory> gameCategories;
	private Integer bonusType;
	private Integer bonusTriggerType; //0:manual/1:deposit/2:login/3:raf
	private Long triggerAmount; //deposit/login # ?
	private Integer triggerGranularity; //1:year/2:month/3:day/4:week/5:total
	private Boolean cancelOnBetBiggerThanBalance;
	private String cancelOnDepositMinimumAmount;
	private BonusRevision dependsOnBonus;
	private List<DepositRequirements> depositRequirements;
	private Domain domain;
	private Boolean enabled;
	private Date expirationDate;
	private String expirationDateTimezone;
	private Integer forDepositNumber;
	private String freeMoneyAmount;
	private Integer freeMoneyWagerRequirement;
	private List<BonusFreeMoney> bonusFreeMoney;
	private List<GamePercentages> gamePercentages;
	private Long id;
	private String maxPayout;
	private Integer maxRedeemable;
	private Integer maxRedeemableGranularity;
	private Integer playThroughRequiredType;
	private Boolean playerMayCancel;
	private Date startingDate;
	private String startingDateTimezone;
	private String timezone;
	private Integer validDays;
	private Boolean visibleToPlayer;
	private String principalEdit;
	private Boolean publicView;
	private GraphicView image;
	private UnlockGames unlockGames;
	private String activationNotificationName;
	private List<BonusExternalGameConfig> bonusExternalGameConfigs;
	private List<BonusToken> bonusTokens;

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BonusExternalGameConfig {
		private Long id;
		private String provider;
		private Long campaignId;
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BonusFreeMoney {
		private Long id;
		private String currency;
		private Long amount;
		private Integer wagerRequirement;
		private Boolean immediateRelease;
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BonusToken {
		private Long id;
		private String currency;
		private Long amount;
		private Double minimumOdds;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UnlockGamesList {
		private Long id;
		private String gameId;
		private String gameGuid;
		private GameInfo gameInfo;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UnlockGames {
		List<UnlockGamesList> games;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GamePercentages {
		@Builder.Default
		private Long id = -1L;
		private String gameId;
		private String gameCategory;
		private GameInfo gameInfo;
		private Integer percentage;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Domain {
		private Long id;
		private String name;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BonusRevision {
		private Long id;
		private Long bonusId;
		private String bonusCode;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FreespinRules {
		private Long id;
		private Integer freespins;
		private Long freeSpinValueInCents;
		private List<FreespinGame> bonusRulesFreespinGames;
		private Integer wagerRequirements;
		private String provider;
		
		@Data
		@Builder
		@ToString
		@EqualsAndHashCode
		@NoArgsConstructor
		@AllArgsConstructor
		public static class FreespinGame {
			@Builder.Default
			private Long id = -1L;
			private String gameId;
			private GameInfo gameInfo;
		}
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InstantRewardRules {
		private Long id;
		private Integer numberOfUnits;
		private Long instantRewardUnitValue;
		private List<InstantRewardGame> bonusRulesInstantRewardGames;
		private Volatility volatility;
		private String provider;

		@Data
		@Builder
		@ToString
		@EqualsAndHashCode
		@NoArgsConstructor
		@AllArgsConstructor
		public static class InstantRewardGame {
			@Builder.Default
			private Long id = -1L;
			private String gameId;
			private GameInfo gameInfo;
		}
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InstantRewardFreespinRules {
		private Long id;
		private Integer numberOfUnits;
		private Long instantRewardUnitValue;
		private List<InstantRewardFreespinGame> bonusRulesInstantRewardFreespinGames;
		private Volatility volatility;
		private String provider;

		@Data
		@Builder
		@ToString
		@EqualsAndHashCode
		@NoArgsConstructor
		@AllArgsConstructor
		public static class InstantRewardFreespinGame {
			@Builder.Default
			private Long id = -1L;
			private String gameId;
			private GameInfo gameInfo;
		}
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CasinoChipRules {
		private Long id;
		private Long casinoChipValue;
		private List<CasinoChipGame> bonusRulesCasinoChipGames;
		private String provider;

		@Data
		@Builder
		@ToString
		@EqualsAndHashCode
		@NoArgsConstructor
		@AllArgsConstructor
		public static class CasinoChipGame {
			@Builder.Default
			private Long id = -1L;
			private String gameId;
			private GameInfo gameInfo;
		}
	}

	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GameInfo {
		private String description;
		private String domainName; //"luckybetz"
		private Boolean enabled; //true
		private String guid; //"service-casino-provider-nucleus_30207"
		private Long id; //1286
		private Map<String, Label> labels;
		private String name; //"Vegas Road Trip"
		private String providerGameId; //"30207"
		private String providerGuid; //"service-casino-provider-nucleus"
		private Boolean visible; //true
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Label {
		private Boolean deleted;
		private String domainName; // "default"
		private Boolean enabled; // false
		private String name; // "category"
		private String value; // "slots"
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DepositRequirements {
		private Integer bonusPercentage;
		@Builder.Default
		private Long id = -1L;
		private String maxDeposit;
		private String minDeposit;
		private Integer wagerRequirements;
	}
}
