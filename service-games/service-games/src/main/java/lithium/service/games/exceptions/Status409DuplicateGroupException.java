package lithium.service.games.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status409DuplicateGroupException extends NotRetryableErrorCodeException {
    public Status409DuplicateGroupException(String message) {
        super(409, message, lithium.service.games.exceptions.Status409DuplicateGroupException.class.getCanonicalName());
    }
}
