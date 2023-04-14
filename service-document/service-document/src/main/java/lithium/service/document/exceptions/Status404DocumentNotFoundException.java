package lithium.service.document.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404DocumentNotFoundException extends NotRetryableErrorCodeException {
    public Status404DocumentNotFoundException(String message) {
        super(404, message, Status404DocumentNotFoundException.class.getCanonicalName());
    }
}
