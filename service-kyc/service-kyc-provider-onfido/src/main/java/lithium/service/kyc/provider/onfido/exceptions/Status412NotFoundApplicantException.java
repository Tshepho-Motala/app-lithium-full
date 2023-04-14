package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412NotFoundApplicantException extends NotRetryableErrorCodeException {
    public Status412NotFoundApplicantException(String message) {
        super(412, message, Status412NotFoundApplicantException.class.getCanonicalName());
    }
}
