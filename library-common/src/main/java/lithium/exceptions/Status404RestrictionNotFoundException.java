package lithium.exceptions;

public class Status404RestrictionNotFoundException extends NotRetryableErrorCodeException {
  public static final int CODE = 404;
  public Status404RestrictionNotFoundException(String message) {
    super(CODE, message, Status404RestrictionNotFoundException.class.getCanonicalName());
  }
}