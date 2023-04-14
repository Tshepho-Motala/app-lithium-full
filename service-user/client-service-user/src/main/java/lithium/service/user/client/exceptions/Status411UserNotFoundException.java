package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411UserNotFoundException extends NotRetryableErrorCodeException {
    public Status411UserNotFoundException(String message) {
        super(411, message, Status411UserNotFoundException.class.getCanonicalName());
    }
}
