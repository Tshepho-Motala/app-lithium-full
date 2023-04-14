package lithium.exceptions;

public class Status405UserDisabledException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 405;

    public Status405UserDisabledException(String message) {
        super(ERROR_CODE, message, Status405UserDisabledException.class.getCanonicalName());
    }

    public Status405UserDisabledException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status405UserDisabledException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
