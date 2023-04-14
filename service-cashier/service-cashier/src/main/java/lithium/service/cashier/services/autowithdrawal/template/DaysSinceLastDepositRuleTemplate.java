package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.DAYS_SINCE_LAST_DEPOSIT;

@Service
public class DaysSinceLastDepositRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return Optional.ofNullable(getLastDeposit(context.guid()))
                .map(transaction -> Days.daysBetween(new DateTime(transaction.getCreatedOn()), DateTime.now()).getDays())
                .map(String::valueOf)
                .map(daysSinceLastDeposit -> validateRule(rule, daysSinceLastDeposit))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return DAYS_SINCE_LAST_DEPOSIT;
    }

}
