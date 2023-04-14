package lithium.service.casino.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.stream.Stream;

public enum PlayerBonusTokenStatus {
	ACTIVE(0, "ACTIVE"),
	CANCELLED(1, "CANCELLED"),
	EXPIRED(2, "EXPIRED"),
	REDEEMED(3, "REDEEMED"),
	RESERVED(4, "RESERVED");

	@Getter
	@Accessors(fluent = true)
	private int code;

	@Getter
	@Accessors(fluent = true)
	private String label;

	PlayerBonusTokenStatus(int code, String label) {
		this.code = code;
		this.label = label;
	}

	@JsonCreator
	public static PlayerBonusTokenStatus fromCode(int pCode) {
		return Stream.of(PlayerBonusTokenStatus.values())
				.filter(status -> status.code == pCode)
				.findFirst()
				.get();
	}
}
