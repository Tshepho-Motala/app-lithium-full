package lithium.exceptions;

public class Status433AccountBlockedPlayerRequestException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 433;

    public Status433AccountBlockedPlayerRequestException(String message) {
        super(ERROR_CODE, message, Status433AccountBlockedPlayerRequestException.class.getCanonicalName());
    }
}
