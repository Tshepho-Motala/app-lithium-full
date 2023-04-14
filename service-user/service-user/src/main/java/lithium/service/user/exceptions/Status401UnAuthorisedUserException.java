package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status401UnAuthorisedUserException extends NotRetryableErrorCodeException {
    public Status401UnAuthorisedUserException(String message) {
        super(401, message, Status401UnAuthorisedUserException.class.getCanonicalName());
    }
}
