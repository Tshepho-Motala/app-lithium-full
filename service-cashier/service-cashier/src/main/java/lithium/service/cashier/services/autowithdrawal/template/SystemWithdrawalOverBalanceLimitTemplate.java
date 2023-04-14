package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.SYSTEM_WD_OVER_BALANCE_LIMIT;

@Service
@Slf4j
public class SystemWithdrawalOverBalanceLimitTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public AutoWithdrawalRuleType getType() {
        return SYSTEM_WD_OVER_BALANCE_LIMIT;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return validateRule(rule, String.valueOf(context.getTransaction().isWdOnBalanceLimitRiched()));
    }
}
