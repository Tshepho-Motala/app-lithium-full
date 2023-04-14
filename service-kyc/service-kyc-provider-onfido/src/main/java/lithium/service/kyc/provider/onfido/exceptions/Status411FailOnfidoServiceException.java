package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411FailOnfidoServiceException extends NotRetryableErrorCodeException {
    public Status411FailOnfidoServiceException(String message) {
        super(411, message, Status411FailOnfidoServiceException.class.getCanonicalName());
    }
}
