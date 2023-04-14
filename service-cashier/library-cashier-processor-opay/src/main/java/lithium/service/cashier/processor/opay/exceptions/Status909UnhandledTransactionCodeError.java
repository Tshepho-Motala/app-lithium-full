package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status909UnhandledTransactionCodeError extends NotRetryableErrorCodeException {
    public Status909UnhandledTransactionCodeError(String message) {
        super(909, message, Status909UnhandledTransactionCodeError.class.getCanonicalName());
    }
}
