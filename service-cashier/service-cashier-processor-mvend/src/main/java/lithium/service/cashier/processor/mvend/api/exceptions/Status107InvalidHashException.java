package lithium.service.cashier.processor.mvend.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status107InvalidHashException extends NotRetryableErrorCodeException {
    public Status107InvalidHashException() {
        super(107, "Invalid hash", Status107InvalidHashException.class.getCanonicalName());
    }
}
