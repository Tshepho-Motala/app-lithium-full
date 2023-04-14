package lithium.service.mail.provider.kickbox.objects;

import java.util.stream.Stream;

public enum VerificationStatus {
  DELIVERABLE("deliverable"),
  UNDELIVERABLE("undeliverable");

  private String status;

  VerificationStatus(String status) {
    this.status = status;
  }

  String getStatus() {
    return  status;
  }

  public static VerificationStatus getFrom(String status) {
    return Stream.of(values()).filter(s -> s.status.equalsIgnoreCase(status)).findFirst().orElse(null);
  }
}
