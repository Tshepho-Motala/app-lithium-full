package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalTransactionNotFoundException extends Exception {
    public PayPalTransactionNotFoundException(String message) {
        super(message);
    }
}
