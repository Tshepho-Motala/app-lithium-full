package lithium.service.cashier.exceptions;

public class TransactionUniqueProcessorReferenceException extends Exception {
    public TransactionUniqueProcessorReferenceException(String transactionId) {
        super("Transaction with defined processor reference already exists: " + transactionId);
    }
}
