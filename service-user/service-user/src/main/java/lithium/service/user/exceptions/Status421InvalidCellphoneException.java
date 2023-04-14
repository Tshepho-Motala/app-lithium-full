package lithium.service.user.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status421InvalidCellphoneException extends ErrorCodeException {

  public static final int CODE = 421;

  public Status421InvalidCellphoneException(String message) {
    super(CODE, message, Status421InvalidCellphoneException.class.getCanonicalName());
  }

  public Status421InvalidCellphoneException(String message, StackTraceElement[] stackTrace) {
    super(CODE, message, Status421InvalidCellphoneException.class.getCanonicalName());
    if (stackTrace != null) {
      super.setStackTrace(stackTrace);
    }
  }
}
