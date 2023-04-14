package lithium.exceptions;

public class Status438PlayTimeLimitConfigurationNotFoundException extends NotRetryableErrorCodeException {
  public static final int CODE = 438;
  public Status438PlayTimeLimitConfigurationNotFoundException(String message) {
    super(CODE, message, Status438PlayTimeLimitConfigurationNotFoundException.class.getCanonicalName());
  }
}
