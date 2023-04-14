package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status424PasswordNotComplexException extends NotRetryableErrorCodeException {
  public Status424PasswordNotComplexException(String message) {
    super(424, message, Status424PasswordNotComplexException.class.getCanonicalName());
  }
}
