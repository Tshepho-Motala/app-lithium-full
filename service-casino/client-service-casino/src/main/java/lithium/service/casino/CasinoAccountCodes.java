package lithium.service.casino;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

// @JsonFormat(shape = JsonFormat.Shape.STRING)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum CasinoAccountCodes {
	PLAYER_BALANCE ("PLAYER_BALANCE"),
	SPORTS_RESERVED_FUNDS ("SPORTS_RESERVED_FUNDS"),
	SPORTS_DEBIT ("SPORTS_DEBIT"),
	SPORTS_BET ("SPORTS_BET"),
	SPORTS_WIN ("SPORTS_WIN"),
	SPORTS_LOSS ("SPORTS_LOSS"),
	SPORTS_FREE_BET ("SPORTS_FREE_BET"),
	SPORTS_FREE_WIN ("SPORTS_FREE_WIN"),
	SPORTS_FREE_LOSS ("SPORTS_FREE_LOSS");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;
	
	public String toString() {
		return value;
	}
}
