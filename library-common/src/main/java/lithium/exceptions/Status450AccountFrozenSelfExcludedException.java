package lithium.exceptions;

public class Status450AccountFrozenSelfExcludedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 450;

    public Status450AccountFrozenSelfExcludedException(String message) {
        super(ERROR_CODE, message, Status450AccountFrozenSelfExcludedException.class.getCanonicalName());
    }
}
