package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status471InsufficientFundsException extends NotRetryableErrorCodeException {
    public static final int CODE = 471;
    public Status471InsufficientFundsException() {
        super(CODE, "Insufficient funds", Status471InsufficientFundsException.class.getCanonicalName());
    }
}
