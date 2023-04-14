package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status902UserNotFoundException extends NotRetryableErrorCodeException {
    public Status902UserNotFoundException() {
        super(902, "User not found", Status902UserNotFoundException.class.getCanonicalName());
    }
}
