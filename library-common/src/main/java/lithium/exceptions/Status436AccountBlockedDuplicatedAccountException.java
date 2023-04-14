package lithium.exceptions;

public class Status436AccountBlockedDuplicatedAccountException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 436;

    public Status436AccountBlockedDuplicatedAccountException(String message) {
        super(ERROR_CODE, message, Status436AccountBlockedDuplicatedAccountException.class.getCanonicalName());
    }
}
