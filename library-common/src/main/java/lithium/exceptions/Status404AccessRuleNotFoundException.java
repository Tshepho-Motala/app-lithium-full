package lithium.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404AccessRuleNotFoundException extends NotRetryableErrorCodeException {
  public static final int CODE = 404;
  public Status404AccessRuleNotFoundException(String message) {
    super(CODE, message, Status404AccessRuleNotFoundException.class.getCanonicalName());
  }
}