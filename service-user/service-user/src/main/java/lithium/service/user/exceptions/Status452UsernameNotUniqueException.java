package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status452UsernameNotUniqueException extends NotRetryableErrorCodeException {
    public Status452UsernameNotUniqueException(String message) {
        super(452, message, Status452UsernameNotUniqueException.class.getCanonicalName());
    }
}
