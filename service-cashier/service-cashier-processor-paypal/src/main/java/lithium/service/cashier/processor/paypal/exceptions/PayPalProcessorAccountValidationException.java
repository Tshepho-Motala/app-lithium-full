package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalProcessorAccountValidationException extends Exception {
    private String extendedMessage;
    public PayPalProcessorAccountValidationException(String message, String extendedMessage) {
        super(message);
        this.extendedMessage = extendedMessage;
    }

    public String getExtendedMessage() {
        return extendedMessage;
    }
}
