package lithium.exceptions;

public class Status492ExcessiveFailedLoginBlockException extends NotRetryableErrorCodeException {

    public Status492ExcessiveFailedLoginBlockException(String message) {
        super(492, message, Status492ExcessiveFailedLoginBlockException.class.getCanonicalName());
    }

    public Status492ExcessiveFailedLoginBlockException(String message, StackTraceElement[] stackTrace) {
        super(492, message, Status492ExcessiveFailedLoginBlockException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
