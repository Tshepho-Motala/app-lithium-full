package lithium.service.cashier.client.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum TransactionProcessingCode {
	HOLD_PENDING_WITHDRAWALS,
	RE_PROCESS_ON_HOLD_WITHDRAWALS,
	APPROVE_WITHDRAWALS,
	CANCEL;
}
