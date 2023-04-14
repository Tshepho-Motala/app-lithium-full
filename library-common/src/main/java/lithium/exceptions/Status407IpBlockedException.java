package lithium.exceptions;

public class Status407IpBlockedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 407;
    public Status407IpBlockedException(String message) {
        super(ERROR_CODE, message, Status407IpBlockedException.class.getCanonicalName());
    }

    public Status407IpBlockedException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status407IpBlockedException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
