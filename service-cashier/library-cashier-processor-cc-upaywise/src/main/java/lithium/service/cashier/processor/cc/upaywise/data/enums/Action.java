package lithium.service.cashier.processor.cc.upaywise.data.enums;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
public enum Action {
	PURCHASE(1, "Purchase", "Automatic capture transaction which is authorized and captured instantly."),
	PRE_AUTHORIZATION(4, "Pre-Authorization", "Authorization transaction which freezes the amount on the customer card."),
	CAPTURE(5, "Capture", "2nd Leg of Pre-Authorization transaction in which the amount will be captured."),
	VOID_PRE_AUTHORIZATION(9, "Void Pre-Authorization", "Cancel the successful Pre-Authorization transaction."),
	REFUND_CREDIT(2, "Refund / Credit", "Refund of purchase or capture transaction.");
	
	Action(int code, String service, String description) {
		this.code = code;
		this.service = service;
		this.description = description;
	}
	
	@Getter
	@Accessors(fluent = true)
	private int code;
	@Getter
	@Accessors(fluent = true)
	private String service;
	@Getter
	@Accessors(fluent = true)
	private String description;
}