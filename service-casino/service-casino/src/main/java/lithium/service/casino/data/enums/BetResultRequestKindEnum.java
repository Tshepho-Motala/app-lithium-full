package lithium.service.casino.data.enums;

import lithium.service.casino.CasinoTranType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum BetResultRequestKindEnum {
	WIN ("WIN"),
	LOSS ("LOSS"),
	VOID ("VOID"),
	FREE_WIN ("FREE_WIN"),
	FREE_LOSS ("FREE_LOSS"),
	BET_REVERSAL("BET_REVERSAL");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;
	
	public String toString() {
		return value;
	}

	public static BetResultRequestKindEnum fromCasinoTranType(CasinoTranType type) {
		switch (type) {
			case CASINO_WIN:
			case CASINO_WIN_JACKPOT:
				return WIN;
			case CASINO_LOSS:
				return LOSS;
			case CASINO_VOID:
				return VOID;
			case REWARD_WIN:
			case CASINO_WIN_FREESPIN:
			case CASINO_WIN_FREEGAME:
			case CASINO_WIN_FREESPIN_JACKPOT:
				return FREE_WIN;
			case REWARD_LOSS:
			case CASINO_LOSS_FREESPIN:
			case CASINO_LOSS_FREEGAME:
				return FREE_LOSS;
			case CASINO_BET_ROLLBACK:
			case REWARD_BET_ROLLBACK:
				return BET_REVERSAL;
			default: return null;
		}
	}
}
