package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status423InvalidUsernameException extends NotRetryableErrorCodeException {
  public Status423InvalidUsernameException(String message) {
    super(423, message, Status423InvalidUsernameException.class.getCanonicalName());
  }
}
