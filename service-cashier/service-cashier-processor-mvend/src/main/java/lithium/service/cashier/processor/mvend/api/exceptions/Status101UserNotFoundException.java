package lithium.service.cashier.processor.mvend.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status101UserNotFoundException extends NotRetryableErrorCodeException {
    public Status101UserNotFoundException() {
        super(101, "User not found", Status101UserNotFoundException.class.getCanonicalName());
    }
}
