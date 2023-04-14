package lithium.service.kyc.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status459VerificationResultNotFountException extends NotRetryableErrorCodeException {
    public Status459VerificationResultNotFountException() {
        super(459, "Fail to get VerificationResult or wrong id", Status459VerificationResultNotFountException.class.getCanonicalName());
    }
}