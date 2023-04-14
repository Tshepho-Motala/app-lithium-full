package lithium.service.user.mass.action.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status400FileStorageException extends NotRetryableErrorCodeException {
    public Status400FileStorageException(String message) {
        super(400, "Could not store file: " + message, Status422DataValidationError.class.getCanonicalName());
    }
}
