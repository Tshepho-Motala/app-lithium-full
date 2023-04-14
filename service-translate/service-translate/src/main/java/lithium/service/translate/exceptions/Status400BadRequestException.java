package lithium.service.translate.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status400BadRequestException extends NotRetryableErrorCodeException {


  public Status400BadRequestException(String message) {
      super(400, message, Status400BadRequestException.class.getCanonicalName());
  }

  public Status400BadRequestException(String message, StackTraceElement[] stackTrace) {
    super(400, message, Status400BadRequestException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
