package lithium.service.cashier.client.objects.enums;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AutoWithdrawalRuleSettingsName {
  private String name;
  private Integer id;
  private AutoWithdrawalRuleFieldType type;
  private List<AutoWithdrawalRuleField.Option> option;
}
