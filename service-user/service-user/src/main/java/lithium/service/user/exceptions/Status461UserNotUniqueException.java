package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status461UserNotUniqueException extends NotRetryableErrorCodeException {
    public Status461UserNotUniqueException(String message) {
        super(461, message, Status461UserNotUniqueException.class.getCanonicalName());
    }
}
