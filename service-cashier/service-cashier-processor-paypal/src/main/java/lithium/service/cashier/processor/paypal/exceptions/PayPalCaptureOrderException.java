package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalCaptureOrderException extends Exception {
    public PayPalCaptureOrderException(String message) {
        super(message);
    }
}
