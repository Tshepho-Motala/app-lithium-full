package lithium.exceptions;

public class Status437AccountBlockedOtherException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 437;

    public Status437AccountBlockedOtherException(String message) {
        super(ERROR_CODE, message, Status437AccountBlockedOtherException.class.getCanonicalName());
    }
}
