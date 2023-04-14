package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status406InvalidVerificationNumberException extends NotRetryableErrorCodeException {
    public Status406InvalidVerificationNumberException(String message) {
        super(406, message, Status406InvalidVerificationNumberException.class.getCanonicalName());
    }
}
