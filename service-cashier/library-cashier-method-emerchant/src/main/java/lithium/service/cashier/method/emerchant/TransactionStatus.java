package lithium.service.cashier.method.emerchant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum TransactionStatus {
	NEW("new", "WPF transaction has been created."),
	TIMEOUT("timeout", "Transaction expired"),
	APPROVED("approved", "Transaction was approved by the schemes and is successful."),
	DECLINED("declined","Transaction was declined by the schemes or risk management."),
	PENDING_ASYNC("pending_async","An asynchronous transaction (3-D secure payment) has been initiated and is waiting for user input. Updates of this state will be sent to the notification url specified in request."),
	PENDING("pending", "The outcome of the transaction could not be determined, e.g. at a timeout situation. Transaction state will eventually change, so make a reconcile after a certain time frame."),
	ERROR("error", "An error has occurred while negotiating with the schemes."),
	REFUNDED("refunded", "Once an approved transaction is refunded the state changes to refunded."),
	CHARGEBACKED("chargebacked", "Once an approved transaction is chargebacked - the state changes to changebacked. Chargeback is the state of rejecting an accepted transaction (with funds transferred) by the cardholder or the issuer"),
	VOIDED("voided", "Transaction was authorized, but later the merchant canceled it."),
	CHARGEBACK_REVERSED("chargeback_reversed", "Once a chargebacked transaction is charged, the state changes to chargeback reversed. Chargeback has been canceled."),
	REPRESENTED("represented", "Once a chargebacked transaction is charged, the state changes to represented. Chargeback has been canceled."),
	SECOND_CHARGEBACKED("second_chargebacked", "Once a chargeback_reversed/represented transaction is chargebacked the state changes to second chargebacked."),
	PENDING_REVIEW("pending_review", "Transaction on hold, a manual review will be done");

	@Setter
	@Accessors(fluent = true)
	private String status;
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String description;

	@JsonValue
	public String status() {
		return status;
	}

	@JsonCreator
	public static TransactionStatus fromStatus(String status) {
		for (TransactionStatus ts : TransactionStatus.values()) {
			if (ts.status.equalsIgnoreCase(status)) {
				return ts;
			}
		}
		return null;
	}
}

