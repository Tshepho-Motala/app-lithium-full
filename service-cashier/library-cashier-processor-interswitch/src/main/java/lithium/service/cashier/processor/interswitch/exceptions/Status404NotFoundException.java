package lithium.service.cashier.processor.interswitch.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404NotFoundException extends NotRetryableErrorCodeException {
    public Status404NotFoundException(String message) {
        super(404, message, Status404NotFoundException.class.getCanonicalName());
    }
}
