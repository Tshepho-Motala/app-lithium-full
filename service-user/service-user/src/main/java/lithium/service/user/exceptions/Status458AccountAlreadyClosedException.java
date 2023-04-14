package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status458AccountAlreadyClosedException extends NotRetryableErrorCodeException {
    public Status458AccountAlreadyClosedException(String message) {
        super(458, message, Status458AccountAlreadyClosedException.class.getCanonicalName());
    }
}
