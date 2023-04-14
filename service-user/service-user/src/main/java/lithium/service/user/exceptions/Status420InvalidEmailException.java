package lithium.service.user.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status420InvalidEmailException extends ErrorCodeException {

  public static final int CODE = 420;

  public Status420InvalidEmailException(String message) {
    super(CODE, message, Status420InvalidEmailException.class.getCanonicalName());
  }

}
