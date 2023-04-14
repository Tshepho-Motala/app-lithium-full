package lithium.service.mail.provider.kickbox.objects;

import java.util.stream.Stream;

public enum VerificationReason {
  INVALID_EMAIL("invalid_email","Specified email is not a valid email address syntax"),
  INVALID_DOMAIN("invalid_domain","Domain name does not exist or is not configured to receive email"),
  REJECTED_EMAIL("rejected_email","Email address was rejected by the SMTP server, email address does not exist"),
  ACCEPTED_EMAIL("accepted_email","Email address was accepted by the SMTP server"),
  LOW_QUALITY("low_quality","Email address has quality issues that may make it a risky or low-value address"),
  LOW_DELIVERABILITY("low_deliverability","Email address appears to be deliverable, but deliverability cannot be guaranteed"),
  NO_CONNECT("no_connect","Could not connect to SMTP server"),
  TIMEOUT("timeout","SMTP session timed out or DNS query timed out"),
  INVALID_SMTP("invalid_smtp","SMTP server returned an unexpected/invalid response"),
  UNAVAILABLE_SMTP("unavailable_smtp","SMTP server was unavailable to process our request"),
  UNEXPECTED_ERROR("unexpected_error","An unexpected error has occurred");

  private String reason;
  private String message;

  VerificationReason(String status, String message) {
    this.reason = status;
    this.message = message;
  }

  public String getReason() {
    return reason;
  }

  public String getMessage() {
    return message;
  }

  public static VerificationReason getFrom(String status) {
    return Stream.of(values()).filter(s -> s.reason.equalsIgnoreCase(status)).findFirst().orElse(null);
  }
}
