package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status407InvalidVerificationIdException extends NotRetryableErrorCodeException {
    public Status407InvalidVerificationIdException(String message) {
        super(407, message, Status407InvalidVerificationIdException.class.getCanonicalName());
    }
}
