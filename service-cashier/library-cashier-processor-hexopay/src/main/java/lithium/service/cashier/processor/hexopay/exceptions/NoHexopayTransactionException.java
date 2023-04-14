package lithium.service.cashier.processor.flutterwave.exceptions;

import lithium.exceptions.ErrorCodeException;

public class NoHexopayTransactionException extends ErrorCodeException {

    public NoHexopayTransactionException(String message) {
        super(420, message, NoHexopayTransactionException.class.getCanonicalName());
    }

    public NoHexopayTransactionException(String message, Throwable cause) {
        super(420, message, cause, NoHexopayTransactionException.class.getCanonicalName());
    }
}
