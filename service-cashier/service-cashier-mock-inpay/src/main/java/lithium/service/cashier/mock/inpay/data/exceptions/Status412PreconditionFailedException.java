package lithium.service.cashier.mock.inpay.data.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412PreconditionFailedException extends NotRetryableErrorCodeException {
    public Status412PreconditionFailedException(String message) {
        super(412, message, Status412PreconditionFailedException.class.getCanonicalName());
    }
}
