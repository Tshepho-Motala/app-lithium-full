package lithium.exceptions;

/**
 *
 */
public class Status415InvalidDocumentTypeException extends NotRetryableErrorCodeException {

  /**
   * @param message
   */
  public Status415InvalidDocumentTypeException(String message) {
    super(415, message, Status415InvalidDocumentTypeException.class.getCanonicalName());
  }
}
