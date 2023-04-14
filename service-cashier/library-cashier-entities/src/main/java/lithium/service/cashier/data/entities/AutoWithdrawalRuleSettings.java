package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "cashier.AutoWithdrawalRuleSettings")
@Builder
@ToString(exclude = "rule")
@EqualsAndHashCode(exclude = "rule")
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "auto_withdrawal_rule_settings",
    indexes = {
        @Index(name = "idx_settings_rule_code", columnList = "rule_id, key", unique = true)
    })
public class AutoWithdrawalRuleSettings {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rule_id",nullable = false)
  @JsonBackReference("rule")
  private AutoWithdrawalRule rule;

  @Column(name = "`key`")
  private String key;

  private String value;
}
