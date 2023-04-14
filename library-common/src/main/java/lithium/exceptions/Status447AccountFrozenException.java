package lithium.exceptions;

public class Status447AccountFrozenException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 447;
    public Status447AccountFrozenException(String message) {
        super(ERROR_CODE, message, Status447AccountFrozenException.class.getCanonicalName());
    }
    public Status447AccountFrozenException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status447AccountFrozenException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
