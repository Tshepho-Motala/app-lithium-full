package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status422InvalidDateOfBirthException extends NotRetryableErrorCodeException {
  public Status422InvalidDateOfBirthException(String message) {
      super(422, message, Status422InvalidDateOfBirthException.class.getCanonicalName());
  }

  public Status422InvalidDateOfBirthException(String message, StackTraceElement[] stackTrace) {
    super(422, message, Status422InvalidDateOfBirthException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
