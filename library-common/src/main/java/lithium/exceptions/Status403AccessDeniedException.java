package lithium.exceptions;

public class Status403AccessDeniedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 403;
    public Status403AccessDeniedException(String message) {
        super(ERROR_CODE, message, Status403AccessDeniedException.class.getCanonicalName());
    }

    public Status403AccessDeniedException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status403AccessDeniedException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
