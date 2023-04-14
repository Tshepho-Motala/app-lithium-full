package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status451UnderageException extends NotRetryableErrorCodeException {
    public Status451UnderageException(String message) {
        super(451, message, Status451UnderageException.class.getCanonicalName());
    }
}
