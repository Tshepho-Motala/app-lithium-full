package lithium.exceptions;

public class Status435AccountBlockedAMLException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 435;

    public Status435AccountBlockedAMLException(String message) {
        super(ERROR_CODE, message, Status435AccountBlockedAMLException.class.getCanonicalName());
    }
}
