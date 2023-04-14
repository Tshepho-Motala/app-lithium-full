package lithium.service.cashier.processor.hexopay.exceptions;

import lithium.exceptions.ErrorCodeException;

public class HexopayInvalidSignatureException extends ErrorCodeException {

    public HexopayInvalidSignatureException(String message) {
        super(421, message, HexopayInvalidSignatureException.class.getCanonicalName());
    }

    public HexopayInvalidSignatureException(String message, Throwable cause) {
        super(421, message, cause, HexopayInvalidSignatureException.class.getCanonicalName());
    }
}
