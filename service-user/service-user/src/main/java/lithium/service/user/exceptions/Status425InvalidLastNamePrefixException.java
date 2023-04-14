package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status425InvalidLastNamePrefixException extends NotRetryableErrorCodeException {
  public static final int CODE = 425;
  public Status425InvalidLastNamePrefixException(String message) {
    super(CODE, message, Status425InvalidLastNamePrefixException.class.getCanonicalName());
  }
}
