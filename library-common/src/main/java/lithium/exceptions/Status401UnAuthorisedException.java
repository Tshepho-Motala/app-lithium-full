package lithium.exceptions;

public class Status401UnAuthorisedException extends NotRetryableErrorCodeException {
    public Status401UnAuthorisedException(String message) {
        super(401, message, Status401UnAuthorisedException.class.getCanonicalName());
    }

    public Status401UnAuthorisedException(String message, StackTraceElement[] stackTrace) {
        super(401, message, Status401UnAuthorisedException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
