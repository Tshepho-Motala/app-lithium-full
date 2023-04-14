package lithium.exceptions;

public class Status430UserUpgradeRequiredInEcosystemException extends NotRetryableErrorCodeException {

  public Status430UserUpgradeRequiredInEcosystemException(final String message) {
    super(430, message, Status430UserUpgradeRequiredInEcosystemException.class.getCanonicalName());
  }
}
