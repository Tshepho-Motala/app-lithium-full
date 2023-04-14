package lithium.exceptions;


public class Status414UserNotFoundException extends NotRetryableErrorCodeException {
  public Status414UserNotFoundException(String message) {
    super(414, message, Status414UserNotFoundException.class.getCanonicalName());
  }

  public Status414UserNotFoundException(String message, StackTraceElement[] stackTrace) {
    super(414, message, Status414UserNotFoundException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
