package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum SystemRestriction {

	LOGIN_BLOCK_AFTER_SE("Login Block After SE", new String[] { RestrictionType.RESTRICTION_LOGIN.code() }, true, 0, false),
	INTERVENTION_COMPS_BLOCK("Intervention Comps Block", new String[] { RestrictionType.RESTRICTION_COMPS.code() }, true, 3, true),
	PLAYER_COMPS_OPTOUT("Player Comps Opt-Out", new String[] { RestrictionType.RESTRICTION_COMPS.code() }, false, 0, false),
	UNDERAGE_COMPS_BLOCK("Underage Comps Block", new String[] { RestrictionType.RESTRICTION_COMPS.code() }, false, 0, false),
	INTERVENTION_CASINO_BLOCK("Intervention Casino Block", new String[] { RestrictionType.RESTRICTION_CASINO.code() }, true, 5, false),
	PLAYER_CASINO_BLOCK("Player Casino Block", new String[] { RestrictionType.RESTRICTION_CASINO.code() }, false, 5, false),
	F2P_BLOCK("F2P Block", new String[] { RestrictionType.RESTRICTION_F2P.code() }, true, 0, false),
	BET_PLACEMENT_BLOCK("Bet Placement Block", new String[] { RestrictionType.RESTRICTION_BET_PLACEMENT.code() }, true, 0, false);
	//KYC_GRACE_PERIOD("KYC Grace Period Exceeded", new String[] { RestrictionType.RESTRICTION_DEPOSIT.code()}),
	//DEPOSIT_THRESHOLD("Deposit Threshold Exceeded", new String[] { RestrictionType.RESTRICTION_DEPOSIT.code(), RestrictionType.RESTRICTION_WITHDRAW.code()}),
	//WITHDRAWAL_THRESHOLD("Withdrawal Threshold Exceeded", new String[] { RestrictionType.RESTRICTION_DEPOSIT.code(), RestrictionType.RESTRICTION_WITHDRAW.code()}),
	//PERIOD_SINCE_DEPOSIT_WITHDRAWAL_THRESHOLD("Period Since Deposit/Withdrawal Threshold Exceeded", new String[] { RestrictionType.RESTRICTION_CASINO.code(), RestrictionType.RESTRICTION_BET_PLACEMENT.code()});

	@Getter
	@Accessors(fluent=true)
	private String restrictionName;

	@Getter
	@Accessors(fluent=true)
	private String[] restrictionCodes;

	@Getter
	@Accessors(fluent=true)
	private Boolean dwhVisible;

	@Getter
	@Accessors(fluent=true)
	private Integer altMessageCount;

	@Getter
	@Accessors(fluent=true)
	private boolean communicateToPlayer;

	public static SystemRestriction findByName(String name) {
		return Arrays.asList(values()).stream().filter(r -> r.restrictionName.equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
