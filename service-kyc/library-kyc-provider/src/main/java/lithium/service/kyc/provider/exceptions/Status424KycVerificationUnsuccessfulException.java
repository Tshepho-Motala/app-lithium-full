package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status424KycVerificationUnsuccessfulException extends NotRetryableErrorCodeException {
    public Status424KycVerificationUnsuccessfulException(String message) {
        super(424, message, Status424KycVerificationUnsuccessfulException.class.getCanonicalName());
    }
}
