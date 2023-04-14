package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status409DuplicateGroupException extends NotRetryableErrorCodeException {
    public Status409DuplicateGroupException(String message) {
        super(409, message, Status409DuplicateGroupException.class.getCanonicalName());
    }
}
