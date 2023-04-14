package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status457CurrentAndNewPasswordMatchException extends NotRetryableErrorCodeException {
    public Status457CurrentAndNewPasswordMatchException(String message) {
        super(457, message, Status457CurrentAndNewPasswordMatchException.class.getCanonicalName());
    }
}
