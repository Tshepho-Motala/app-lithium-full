package lithium.service.cashier.processor.mvend.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status901UserNotFoundException extends NotRetryableErrorCodeException {
    public Status901UserNotFoundException() {
        super(901, "User not found", Status901UserNotFoundException.class.getCanonicalName());
    }
}
