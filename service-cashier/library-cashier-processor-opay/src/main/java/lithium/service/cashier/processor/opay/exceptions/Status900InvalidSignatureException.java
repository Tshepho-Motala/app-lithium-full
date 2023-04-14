package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status900InvalidSignatureException extends NotRetryableErrorCodeException {
    public Status900InvalidSignatureException(String message) {
        super(900, message, Status900InvalidSignatureException.class.getCanonicalName());
    }
}
