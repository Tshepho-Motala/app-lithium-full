package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status428KycMismatchLastNameException extends NotRetryableErrorCodeException {
    public Status428KycMismatchLastNameException() {
        super(428, "We're unable to verify your account. Your account name does not match the name of your submitted verification method. Please contact us to complete the process", Status428KycMismatchLastNameException.class.getCanonicalName());
    }
}
