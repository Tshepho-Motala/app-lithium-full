package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status409DuplicateTagNameExistException extends NotRetryableErrorCodeException {
  public static final int CODE = 409;
  public Status409DuplicateTagNameExistException(String message) {
    super(CODE, message, Status409DuplicateTagNameExistException.class.getCanonicalName());
  }
}
