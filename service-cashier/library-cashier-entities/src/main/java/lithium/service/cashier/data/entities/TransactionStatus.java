package lithium.service.cashier.data.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "code"})
@Table(
    catalog = "lithium_cashier",
    name = "transaction_status"
)
public class TransactionStatus implements Serializable {

  private static final long serialVersionUID = -2082517463977013077L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = false, unique = true)
  @Size(min = 2, max = 35)
  @Pattern(regexp = "^[a-zA-Z0-9_]+$")
  private String code;

  @Column(nullable = true)
  private String description;

  @Column(nullable = false)
  private Boolean active;

  @Column(nullable = false)
  private Boolean deleted;

  @PrePersist
  private void prePersist() {
    if (this.deleted == null) {
      this.deleted = false;
    }
  }

  public boolean codeIs(String... codes) {
    for (String c : codes) {
      if (c.equalsIgnoreCase(code)) {
        return true;
      }
    }
    return false;
  }

  public boolean isStart() {
    return "START".equals(code);
  }

  public boolean isSuccess() {
    return "SUCCESS".equals(code);
  }

  public boolean isApproved() {
    return "APPROVED".equals(code);
  }

  public boolean isAutoApproved() {
    return "AUTO_APPROVED".equals(code);
  }

  public boolean isDeclined() {
    return "DECLINED".equals(code);
  }

  public boolean isFatalError() {
    return "FATALERROR".equals(code);
  }

  public boolean isCancelled() {
    return "CANCEL".equals(code);
  }

  public boolean isPlayerCancelled() {
    return "PLAYER_CANCEL".equals(code);
  }

  public boolean isWaitForApproval() { return "WAITFORAPPROVAL".equals(code); }

  public boolean isOnHold() { return "ON_HOLD".equals(code); }

  public boolean isAbleToHold() {
    return  isWaitForApproval() || isAutoApprovedDelayed();
  }

  public boolean isAbleToApprove() {
    return isWaitForApproval()
        || isOnHold()
        || isAutoApprovedDelayed();
  }

  public boolean isAutoApprovedDelayed() { return "AUTO_APPROVED_DELAYED".equals(code); }

  public boolean isWaitForProcessor() { return "WAITFORPROCESSOR".equals(code); }

    public static List<String> getPengingStatusCodes() {
    return Arrays.asList("WAITFORAPPROVAL", "AUTO_APPROVED_DELAYED");
  }
}
