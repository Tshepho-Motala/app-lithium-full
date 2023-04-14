package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404NoSuchUserException extends NotRetryableErrorCodeException {
    public Status404NoSuchUserException() {
        super(404, "User not found", Status404NoSuchUserException.class.getCanonicalName());
    }
}
