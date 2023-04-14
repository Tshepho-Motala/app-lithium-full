package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalInvalidSignatureException extends Exception {
    public PayPalInvalidSignatureException(String message) {
        super(message);
    }
}
