package lithium.service.cashier.client.objects.autowithdrawal;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AutoWithdrawalInitData {
    private AutoWithdrawalRuleType ruleType;
    private Map<Integer, AutoWithdrawalRuleField> settings;
    private AutoWithdrawalRuleField operator;
    private AutoWithdrawalRuleField value;
}
