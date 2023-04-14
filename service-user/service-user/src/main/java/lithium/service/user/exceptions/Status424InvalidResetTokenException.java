package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status424InvalidResetTokenException extends NotRetryableErrorCodeException {
    public Status424InvalidResetTokenException(String message) {
        super(424, message, Status424InvalidResetTokenException.class.getCanonicalName());
    }
}
