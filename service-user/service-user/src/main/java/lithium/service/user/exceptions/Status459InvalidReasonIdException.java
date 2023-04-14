package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status459InvalidReasonIdException extends NotRetryableErrorCodeException {
    public Status459InvalidReasonIdException(String message) {
        super(459, message, Status459InvalidReasonIdException.class.getCanonicalName());
    }
}
