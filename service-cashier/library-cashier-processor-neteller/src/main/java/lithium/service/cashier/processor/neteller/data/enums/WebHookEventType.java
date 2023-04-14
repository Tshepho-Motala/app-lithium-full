package lithium.service.cashier.processor.neteller.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum WebHookEventType {
	PAYMENT_HANDLE_PAYABLE("PAYMENT_HANDLE_PAYABLE"),
	PAYMENT_HANDLE_COMPLETED("PAYMENT_HANDLE_COMPLETED"),
	PAYMENT_HANDLE_FAILED("PAYMENT_HANDLE_FAILED"),
	PAYMENT_COMPLETED("PAYMENT_COMPLETED"),
	PAYMENT_HELD("PAYMENT_HELD"),
	PAYMENT_FAILED("PAYMENT_FAILED"),
	SETTLEMENT_COMPLETED("SETTLEMENT_COMPLETED"),
	SETTLEMENT_FAILED("SETTLEMENT_FAILED"),
	SA_CREDIT_COMPLETED("SA_CREDIT_COMPLETED"),
	SA_CREDIT_HELD("SA_CREDIT_HELD"),
	SA_CREDIT_FAILED("SA_CREDIT_FAILED"),
	SA_CREDIT_CANCELLED("SA_CREDIT_CANCELLED"),
	SA_CREDIT_PENDING("SA_CREDIT_PENDING");

	@Setter
	@Accessors(fluent=true)
	private String event;

	@JsonCreator
	public static WebHookEventType fromAction(String event) {
		for (WebHookEventType e: WebHookEventType.values()) {
			if (e.event.equalsIgnoreCase(event)) {
				return e;
			}
		}
		return null;
	}
}
