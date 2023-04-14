package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status903ReferenceExistsException extends NotRetryableErrorCodeException {
    public Status903ReferenceExistsException() {
        super(903, "A transaction with this reference already exists.", Status903ReferenceExistsException.class.getCanonicalName());
    }
}
