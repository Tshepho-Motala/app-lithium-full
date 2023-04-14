package lithium.service.cashier.processor.paystack.exeptions;

public class PaystackWrongConfigurationException extends Exception {
    public PaystackWrongConfigurationException(String message) {
        super(message);
    }
}
