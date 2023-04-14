package lithium.exceptions;

public class Status409DuplicateTransactionException extends NotRetryableErrorCodeException {
    public static final int CODE = 409;

    private String transactionId;

    public Status409DuplicateTransactionException(String transactionId, String message) {
        super(CODE, message, Status409DuplicateTransactionException.class.getCanonicalName());
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
