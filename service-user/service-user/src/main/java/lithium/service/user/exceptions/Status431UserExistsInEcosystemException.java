package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status431UserExistsInEcosystemException extends NotRetryableErrorCodeException {
    public Status431UserExistsInEcosystemException(String message) {
        super(431, message, Status431UserExistsInEcosystemException.class.getCanonicalName());
    }
}
