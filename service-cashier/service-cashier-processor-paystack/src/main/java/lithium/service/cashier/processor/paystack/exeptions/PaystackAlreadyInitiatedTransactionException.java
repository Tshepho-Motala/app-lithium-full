package lithium.service.cashier.processor.paystack.exeptions;

public class PaystackAlreadyInitiatedTransactionException extends Exception {
    public PaystackAlreadyInitiatedTransactionException() {
        super("Transaction already initiated with this reference");
    }
}
