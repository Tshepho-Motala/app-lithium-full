package lithium.service.cashier.data.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = "ruleset")
@EqualsAndHashCode(exclude = "ruleset")
@Table(
    catalog = "lithium_cashier",
    name = "auto_withdrawal_rule_set_process",
    indexes = {
        @Index(name = "idx_created", columnList = "created", unique = false),
        @Index(name = "idx_started", columnList = "started", unique = false),
        @Index(name = "idx_completed", columnList = "completed", unique = false)
    })
public class AutoWithdrawalRuleSetProcess {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @ManyToOne
  @JoinColumn(nullable = false)
  private AutoWithdrawalRuleSet ruleset;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date created;

  @Column(nullable = true)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date started;

  @Column(nullable = true)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date completed;

  @PrePersist
  private void prePersist() {
    if (created == null) {
      created = new Date();
    }
  }
}
