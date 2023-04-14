package lithium.service.cashier.client.objects;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum TransactionType implements Serializable {
	
	DEPOSIT(100, "Deposit", ""),
	WITHDRAWAL(200, "Withdrawal", ""),
	REVERSAL(300, "reversal", "");
	
	@Getter
	@Accessors(fluent = true)
	private Integer code;
	@Getter
	@Accessors(fluent = true)
	private String description;
	@Getter
	@Accessors(fluent = true)
	private String translation;

	public static TransactionType fromDescription(String description) {
		if (description == null) return null;
		for (TransactionType tt:TransactionType.values()) {
			if (tt.description.toUpperCase().startsWith(description.toUpperCase())) {
				return tt;
			}
		}
		return null;
	}
}
