package lithium.service.cashier.processor.flutterwave.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status500VerifyException extends ErrorCodeException {

    public Status500VerifyException(String message) {
        super(500, message, Status500VerifyException.class.getCanonicalName());
    }

    public Status500VerifyException(String message, Throwable cause) {
        super(500, message, cause, Status500VerifyException.class.getCanonicalName());
    }
}
