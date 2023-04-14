package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.LAST_DEPOSIT_AMOUNT_CENTS;

@Service
public class LastDepositAmountCentsRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return Optional.ofNullable(getLastDeposit(context.guid()))
                .map(Transaction::getAmountCents)
                .map(String::valueOf)
                .map(amount -> validateRule(rule, amount))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return LAST_DEPOSIT_AMOUNT_CENTS;
    }

}
