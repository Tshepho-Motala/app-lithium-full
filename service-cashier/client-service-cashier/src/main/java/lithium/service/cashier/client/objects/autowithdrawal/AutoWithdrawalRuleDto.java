package lithium.service.cashier.client.objects.autowithdrawal;

import lithium.service.cashier.client.objects.RuleValue;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString(exclude = "ruleset")
@EqualsAndHashCode(exclude = "ruleset")
@NoArgsConstructor
@AllArgsConstructor
public class AutoWithdrawalRuleDto {

  private Long id;
  private int version;
  private Long rulesetId;

  private boolean enabled;
  private boolean deleted;
  private AutoWithdrawalRuleType field;
  private List<AutoWithdrawalRuleSettingsDto> settings;
  private AutoWithdrawalRuleOperator operator;
  private List<RuleValue> value;
  private String value2; // Used for the BETWEEN operator

}
