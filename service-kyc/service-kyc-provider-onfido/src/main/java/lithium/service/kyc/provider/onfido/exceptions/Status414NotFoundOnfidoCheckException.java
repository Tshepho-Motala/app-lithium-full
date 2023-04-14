package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status414NotFoundOnfidoCheckException extends NotRetryableErrorCodeException {
    public Status414NotFoundOnfidoCheckException(String message) {
        super(414, message, Status414NotFoundOnfidoCheckException.class.getCanonicalName());
    }
}
