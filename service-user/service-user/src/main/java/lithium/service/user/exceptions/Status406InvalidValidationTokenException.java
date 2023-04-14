package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status406InvalidValidationTokenException extends NotRetryableErrorCodeException {
    public Status406InvalidValidationTokenException(String message) {
        super(406, message, Status406InvalidValidationTokenException.class.getCanonicalName());
    }
}
