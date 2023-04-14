package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status427UserKycVerificationLifetimeAttemptsExceeded extends NotRetryableErrorCodeException {
    public Status427UserKycVerificationLifetimeAttemptsExceeded(String message) {
        super(427, message, Status427UserKycVerificationLifetimeAttemptsExceeded.class.getCanonicalName());
    }
}
