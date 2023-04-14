package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status515SignatureCalculationException extends NotRetryableErrorCodeException {
    public Status515SignatureCalculationException() {
        super(515, "Can't calculate signature", Status515SignatureCalculationException.class.getCanonicalName());
    }
}
