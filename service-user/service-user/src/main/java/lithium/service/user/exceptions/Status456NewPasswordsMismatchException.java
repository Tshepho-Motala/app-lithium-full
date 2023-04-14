package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status456NewPasswordsMismatchException extends NotRetryableErrorCodeException {
    public Status456NewPasswordsMismatchException(String message) {
        super(456, message, Status456NewPasswordsMismatchException.class.getCanonicalName());
    }
}
