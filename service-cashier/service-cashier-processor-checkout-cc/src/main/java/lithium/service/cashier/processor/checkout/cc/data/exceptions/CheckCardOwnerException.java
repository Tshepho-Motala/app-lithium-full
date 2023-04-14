package lithium.service.cashier.processor.checkout.cc.data.exceptions;

import lithium.exceptions.ErrorCodeException;

public class CheckCardOwnerException extends ErrorCodeException {
    public CheckCardOwnerException(String message) {
        super(402, message, CheckCardOwnerException.class.getCanonicalName());
    }

    public CheckCardOwnerException(String message, Throwable cause) {
        super(402, message, cause, CheckCardOwnerException.class.getCanonicalName());
    }
}
