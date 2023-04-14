package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status423InvalidPasswordException extends NotRetryableErrorCodeException {
    public Status423InvalidPasswordException(String message) {
        super(423, message, Status423InvalidPasswordException.class.getCanonicalName());
    }
}
