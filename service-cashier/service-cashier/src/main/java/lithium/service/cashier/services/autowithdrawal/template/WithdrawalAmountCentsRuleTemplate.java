package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.WITHDRAW_AMOUNT_CENTS;

@Service
public class WithdrawalAmountCentsRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return validateRule(rule, String.valueOf(context.getTransaction().getAmountCents()));
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return WITHDRAW_AMOUNT_CENTS;
    }

}
