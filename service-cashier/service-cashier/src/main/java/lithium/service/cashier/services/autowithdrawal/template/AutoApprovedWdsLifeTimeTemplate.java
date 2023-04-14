package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.AUTO_APPROVED_WDS;

@Service
@Slf4j
public class AutoApprovedWdsLifeTimeTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public AutoWithdrawalRuleType getType() {
        return AUTO_APPROVED_WDS;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        List<Transaction> transactions = getTransactionService().findAllTransactions(context.guid(), TransactionType.WITHDRAWAL, DoMachineState.getFinalStateCodes());
        Long maxAutoApprovedWdsCount = getMaxConsecutiveAutoApprovedTransactionsCount(transactions);
        log.debug("Max auto approved count = " + maxAutoApprovedWdsCount + " from " + transactions.size() + " withdrawals");
        return validateRule(rule, String.valueOf(maxAutoApprovedWdsCount));
    }
}
