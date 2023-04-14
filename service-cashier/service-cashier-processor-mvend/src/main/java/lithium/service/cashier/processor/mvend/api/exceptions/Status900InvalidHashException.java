package lithium.service.cashier.processor.mvend.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status900InvalidHashException extends NotRetryableErrorCodeException {
    public Status900InvalidHashException() {
        super(900, "Invalid hash", Status900InvalidHashException.class.getCanonicalName());
    }
}
