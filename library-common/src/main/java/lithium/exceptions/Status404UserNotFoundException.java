package lithium.exceptions;

public class Status404UserNotFoundException extends NotRetryableErrorCodeException {
    public Status404UserNotFoundException(String message) {
        super(404, message, Status404UserNotFoundException.class.getCanonicalName());
    }

  public Status404UserNotFoundException(String message, StackTraceElement[] stackTrace) {
    super(404, message, Status404UserNotFoundException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
