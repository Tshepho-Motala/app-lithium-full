package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.converter.EnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity
@Builder
@ToString(exclude = "ruleset")
@EqualsAndHashCode(exclude = "ruleset")
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "auto_withdrawal_rule",
    indexes = {
        @Index(name = "idx_ruleset_field", columnList = "ruleset_id, field", unique = true),
        @Index(name = "idx_deleted", columnList = "deleted", unique = false)
    })
public class AutoWithdrawalRule {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonBackReference("ruleset")
  private AutoWithdrawalRuleSet ruleset;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false)
  private boolean deleted;

  @Convert(converter = EnumConverter.RuleTypeConverter.class)
  private AutoWithdrawalRuleType field;

  @Fetch(FetchMode.SELECT)
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "rule", cascade = CascadeType.MERGE)
  @JsonManagedReference("rule")
  private List<AutoWithdrawalRuleSettings> settings;

  @Column(nullable = false)
  @Convert(converter = EnumConverter.OperatorConverter.class)
  private AutoWithdrawalRuleOperator operator;

  @Column(nullable = false, length = 1000) // When used with the IN operator, this should hold a comma separated list
  private String value;

  @Column(nullable = true)
  private String value2; // Used for the BETWEEN operator

  public Optional<String> getSettingValueByKey(String name) {
    return this.getSettings()
        .stream()
        .filter(s -> s.getKey().equals(name))
        .findFirst()
        .map(AutoWithdrawalRuleSettings::getValue);
  }
}
