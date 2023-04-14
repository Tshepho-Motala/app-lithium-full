package lithium.service.cashier.processor.checkout.cc.data.exceptions;

import lithium.exceptions.ErrorCodeException;

public class CheckoutSignatureVerifyException extends ErrorCodeException {
    public CheckoutSignatureVerifyException(String message) {
        super(401, message, CheckoutSignatureVerifyException.class.getCanonicalName());
    }

    public CheckoutSignatureVerifyException(String message, Throwable cause) {
        super(401, message, cause, CheckoutSignatureVerifyException.class.getCanonicalName());
    }
}
