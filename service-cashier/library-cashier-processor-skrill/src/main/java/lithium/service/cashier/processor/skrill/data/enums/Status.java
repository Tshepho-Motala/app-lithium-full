package lithium.service.cashier.processor.skrill.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Status {
	PROCESSED("2", "Processed", "Sent when the transaction is processed and the funds have been received in your Skrill account."),
	PENDING("0", "Pending", "Sent when the customers pays via an offline bank transfer option. Such transactions will auto-process if the bank transfer is received by Skrill."),
	CANCELLED("-1", "Cancelled", "Pending transactions can either be cancelled manually by the sender in their online Skrill Digital Wallet account history or they will auto-cancel after 14 days if still pending."),
	FAILED("-2", "Failed", "This status is typically sent when the customer tries to pay via Credit Card or Direct Debit but our provider declines the transaction. It can also be sent if the transaction is declined by Skrill’s internal fraud engine for example: failed_reason_code 54 - Failed due to internal security restrictions."),
	CHARGEBACK("-3", "Chargeback", "Whenever a chargeback is received by Skrill, a ‘-3’ status is posted in the status_url and an email is sent to the primary email address linked to the Merchant’s account. Skrill also creates a new debit transaction to debit the funds from your merchant account.");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String code;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String status;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String description;

	@JsonCreator
	public static Status fromCode(String code) {
		for (Status s: Status.values()) {
			if (s.code.equalsIgnoreCase(code)) {
				return s;
			}
		}
		return null;
	}
}
