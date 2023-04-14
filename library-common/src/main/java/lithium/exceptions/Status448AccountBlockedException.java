package lithium.exceptions;

public class Status448AccountBlockedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 448;

    public Status448AccountBlockedException(String message) {
        super(ERROR_CODE, message, Status448AccountBlockedException.class.getCanonicalName());
    }
}
