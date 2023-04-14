package lithium.service.user.mass.action.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404MassActionNotFoundException extends NotRetryableErrorCodeException {
    public Status404MassActionNotFoundException(String message) {
        super(404, "Mass action not found (UploadStatus needs to be in CHECKED state to be found): " + message, Status404MassActionNotFoundException.class.getCanonicalName());
    }
}
