package lithium.service.cashier.method.premierpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Action {
	PAYMENT("PAYMENT"),
	REFUND("REFUND"),
	PENDING("PENDING"),
	PAYOUT("PAYOUT"),
	PENDING_PAYOUT("PENDING_PAYOUT");
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String code;
	
	@JsonCreator
	public static Action fromCode(String code) {
		for (Action ec : Action.values()) {
			if (ec.code.equalsIgnoreCase(code)) {
				return ec;
			}
		}
		return null;
	}
}
