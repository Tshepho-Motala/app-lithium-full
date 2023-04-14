package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
public enum EBalanceAdjustmentResponseStatus implements Serializable {
	SUCCESS(200),
	TRANSACTION_DATA_VALIDATION_ERROR(414),
	NEGATIVE_BALANCE_ERROR(415),
	INSUFFICIENT_FUNDS(471),
	INTERNAL_ERROR(500);

	@Getter
	private int code;

	public static EBalanceAdjustmentResponseStatus fromCode(int code) {
		for (EBalanceAdjustmentResponseStatus status: EBalanceAdjustmentResponseStatus.values()) {
			if (status.getCode() == code) return status;
		}
		return null;
	}
}
