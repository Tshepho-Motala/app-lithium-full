package lithium.service.limit.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum LimitTranType {
	TRANSFER_TO_BALANCE_LIMIT_ESCROW ("TRANSFER_TO_BALANCE_LIMIT_ESCROW"),
	TRANSFER_FROM_BALANCE_LIMIT_ESCROW ("TRANSFER_FROM_BALANCE_LIMIT_ESCROW");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;

	public String toString() {
		return value;
	}
}
