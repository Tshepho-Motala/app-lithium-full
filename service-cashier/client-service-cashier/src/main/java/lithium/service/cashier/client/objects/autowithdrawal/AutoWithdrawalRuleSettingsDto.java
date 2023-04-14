package lithium.service.cashier.client.objects.autowithdrawal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoWithdrawalRuleSettingsDto {
  private Long id;
  private int version;
  private String key;
  private String value;
}
