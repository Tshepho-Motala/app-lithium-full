package lithium.service.cashier.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status407TransactionInFinalStateException extends NotRetryableErrorCodeException {
	public Status407TransactionInFinalStateException() {
		super(407, "The transaction is already in a final state",
			Status407TransactionInFinalStateException.class.getCanonicalName());
	}
}
