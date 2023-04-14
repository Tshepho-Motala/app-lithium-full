package lithium.exceptions;

public class Status446AccountFrozenCRUKSSelfExcludedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 446;

    public Status446AccountFrozenCRUKSSelfExcludedException(String message) {
        super(ERROR_CODE, message, Status446AccountFrozenCRUKSSelfExcludedException.class.getCanonicalName());
    }
}
