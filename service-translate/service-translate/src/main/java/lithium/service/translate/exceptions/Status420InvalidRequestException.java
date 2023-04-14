package lithium.service.translate.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status420InvalidRequestException extends ErrorCodeException {

  public static final int CODE = 420;

  public Status420InvalidRequestException(String message) {
    super(CODE, message, Status420InvalidRequestException.class.getCanonicalName());
  }

}
