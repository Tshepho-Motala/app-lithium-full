package lithium.service.cashier.processor.interswitch.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status417LimistsException  extends NotRetryableErrorCodeException {
    public Status417LimistsException(String message) {
        super(417, message, Status417LimistsException.class.getCanonicalName());
    }
}
