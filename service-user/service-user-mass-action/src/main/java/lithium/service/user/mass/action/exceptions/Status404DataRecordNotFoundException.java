package lithium.service.user.mass.action.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404DataRecordNotFoundException extends NotRetryableErrorCodeException {
    public Status404DataRecordNotFoundException(String message) {
        super(404, "File upload data record not found: " + message, Status404DataRecordNotFoundException.class.getCanonicalName());
    }
}