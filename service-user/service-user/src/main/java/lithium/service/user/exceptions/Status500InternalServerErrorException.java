package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status500InternalServerErrorException extends NotRetryableErrorCodeException {
  public Status500InternalServerErrorException(String message) {
      super(500, message, Status500InternalServerErrorException.class.getCanonicalName());
  }

  public Status500InternalServerErrorException(String message, StackTraceElement[] stackTrace) {
    super(500, message, Status500InternalServerErrorException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
