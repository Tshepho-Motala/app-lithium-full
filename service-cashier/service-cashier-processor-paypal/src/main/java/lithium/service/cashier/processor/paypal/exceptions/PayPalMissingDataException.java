package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalMissingDataException extends Exception {
    public PayPalMissingDataException(String message) {
        super(message);
    }
}
