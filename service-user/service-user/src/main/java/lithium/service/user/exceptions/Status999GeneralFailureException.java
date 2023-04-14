package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status999GeneralFailureException extends NotRetryableErrorCodeException {
    public Status999GeneralFailureException(String message) {
        super(999, "General failure: " + message, Status999GeneralFailureException.class.getCanonicalName());
    }
}
