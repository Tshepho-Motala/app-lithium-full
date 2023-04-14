package lithium.exceptions;

public class Status465DomainUnknownCountryException extends NotRetryableErrorCodeException {

  public static final int ERROR_CODE = 465;

  public Status465DomainUnknownCountryException(String message) {
    super(ERROR_CODE, message, Status465DomainUnknownCountryException.class.getCanonicalName());
  }
}
