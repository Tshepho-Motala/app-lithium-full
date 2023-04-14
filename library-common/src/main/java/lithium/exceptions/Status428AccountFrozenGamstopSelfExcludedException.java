package lithium.exceptions;

public class Status428AccountFrozenGamstopSelfExcludedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 428;

    public Status428AccountFrozenGamstopSelfExcludedException(String message) {
        super(ERROR_CODE, message, Status428AccountFrozenGamstopSelfExcludedException.class.getCanonicalName());
    }
}
