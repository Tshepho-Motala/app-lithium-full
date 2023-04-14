package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status426PlayerUnderAgeException extends NotRetryableErrorCodeException {
    public Status426PlayerUnderAgeException(String message) {
        super(426, message, Status426PlayerUnderAgeException.class.getCanonicalName());
    }
}
