package lithium.exceptions;

public class Status453EmailNotUniqueException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 453;

    public Status453EmailNotUniqueException(String message) {
        super(ERROR_CODE, message, Status453EmailNotUniqueException.class.getCanonicalName());
    }
}
