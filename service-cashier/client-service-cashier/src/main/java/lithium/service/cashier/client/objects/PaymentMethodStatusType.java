package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static java.util.Objects.isNull;

@ToString
@AllArgsConstructor()
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentMethodStatusType {
	ACTIVE("ACTIVE", "No restrictions on that payment method"),
	BLOCKED("BLOCKED", "Deposits and withdrawals are forbidden"),
	DISABLED("DISABLED", "Payment methods is disabled for the current player and can be used another one"),
	DEPOSIT_ONLY("DEPOSIT_ONLY", "Deposits are allowed, withdrawals are forbidden"),
	WITHDRAWAL_ONLY("WITHDRAWAL_ONLY", "Deposits are forbidden, withdrawals are allowed"),
	HISTORIC("HISTORIC", "Payment method is historic"),
	EXPIRED("EXPIRED", "Payment method is expired");

	private String name;

	public static boolean isDisabled(PaymentMethodStatusType status) {
		return isNull(status) || DISABLED.equals(status) || HISTORIC.equals(status);
	}

	@JsonValue
	public String getName() {
		return name;
	}

	@Getter
	@Accessors(fluent=true)
	private String description;

	@JsonCreator
	public static PaymentMethodStatusType fromName(String name) {
		for (PaymentMethodStatusType s: PaymentMethodStatusType.values()) {
			if (s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}

    public static boolean isActiveAccountStatus(PaymentMethodStatusType status, Boolean isDeposit) {
        return status == PaymentMethodStatusType.ACTIVE
                || status == PaymentMethodStatusType.DEPOSIT_ONLY && (isDeposit == null || isDeposit)
                || status == PaymentMethodStatusType.WITHDRAWAL_ONLY && (isDeposit == null || !isDeposit);
    }

    public static boolean isActiveOrHistoricAccountStatus(PaymentMethodStatusType status, Boolean isDeposit) {
        return isActiveAccountStatus(status, isDeposit)
                || status == PaymentMethodStatusType.HISTORIC;

    }
}
