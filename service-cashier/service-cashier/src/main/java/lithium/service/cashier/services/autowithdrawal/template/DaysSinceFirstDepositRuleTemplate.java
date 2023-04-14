package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.DAYS_SINCE_FIRST_DEPOSIT;

@Service
public class DaysSinceFirstDepositRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        Transaction firstDeposit = getTransactionService().findFirstTransaction(context.guid(), TransactionType.DEPOSIT, DoMachineState.SUCCESS.name());
        return Optional.ofNullable(firstDeposit)
                .map(transaction -> Days.daysBetween(new DateTime(transaction.getCreatedOn()), DateTime.now()).getDays())
                .map(String::valueOf)
                .map(daysSinceFirstDeposit -> validateRule(rule, daysSinceFirstDeposit))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return DAYS_SINCE_FIRST_DEPOSIT;
    }

}
