package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412LoginEventNotFoundException extends NotRetryableErrorCodeException {
    public Status412LoginEventNotFoundException(String message) {
        super(412, message, Status412LoginEventNotFoundException.class.getCanonicalName());
    }
}
