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

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.MANUALLY_APPROVED_WDS;

@Service
@Slf4j
public class ManuallyApprovedWdsLifeTimeTemplate extends AutoWithdrawalRuleTemplate  {
    @Override
    public AutoWithdrawalRuleType getType() {
        return MANUALLY_APPROVED_WDS;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        List<Transaction> transactions = getTransactionService().findAllTransactions(context.guid(), TransactionType.WITHDRAWAL, DoMachineState.getFinalStateCodes());
        Long maxManuallyApprovedWdsCount = getMaxConsecutiveManuallyApprovedTransactionsCount(transactions);
        log.debug("Max manually approved count = " + maxManuallyApprovedWdsCount + " from "+transactions.size() + " withdrawals");
        return validateRule(rule, String.valueOf(maxManuallyApprovedWdsCount));
    }
}
