package lithium.exceptions;

/**
 *
 */
public class Status413PayloadTooLargeException extends NotRetryableErrorCodeException {

  /**
   * @param message
   */
  public Status413PayloadTooLargeException(String message) {
    super(413, message, Status413PayloadTooLargeException.class.getCanonicalName());
  }
}
