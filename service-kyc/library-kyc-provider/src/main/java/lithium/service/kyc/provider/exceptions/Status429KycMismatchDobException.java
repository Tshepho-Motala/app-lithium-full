package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status429KycMismatchDobException extends NotRetryableErrorCodeException {
    public Status429KycMismatchDobException() {
        super(429, "We're unable to verify your account. Your account date of birth does not match the date of birth of your submitted verification method. Please contact us to complete the process." , Status429KycMismatchDobException.class.getCanonicalName());
    }
}
