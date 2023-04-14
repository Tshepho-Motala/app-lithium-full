package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.MANUALLY_APPROVED_WDS_LIMITED;

@Service
@Slf4j
public class ManuallyApprovedWdsLimitedTemplate extends AutoWithdrawalRuleTemplate {

    private static final String NUMBER_OF_WITHDRAWAL = "NUMBER_OF_WITHDRAWAL";
    @Override
    public AutoWithdrawalRuleType getType() {
        return MANUALLY_APPROVED_WDS_LIMITED;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        String setting = rule.getSettingValueByKey(NUMBER_OF_WITHDRAWAL).get();
        PageRequest pageRequest = PageRequest.of(0, Integer.valueOf(setting));
        Page<Transaction> transactions = getTransactionService().findByUserGuidAndTransactionTypeAndStatusCodeInOrderByIdDesc (context.guid(), TransactionType.WITHDRAWAL, DoMachineState.getFinalStateCodes(), pageRequest);
        Long maxManuallyApprovedWdsCount = getMaxConsecutiveManuallyApprovedTransactionsCount(transactions.getContent());
        log.debug("Max manually approved count = " + maxManuallyApprovedWdsCount + " from "+transactions.getContent().size() + " withdrawals");
        return validateRule(rule, String.valueOf(maxManuallyApprovedWdsCount));
    }
    @Override
    public List<AutoWithdrawalRuleField> getAutoWithdrawalRuleSettings() {
        return Arrays.asList(AutoWithdrawalRuleField.builder()
                .id(1)
                .key(NUMBER_OF_WITHDRAWAL)
                .type(AutoWithdrawalRuleFieldType.LONG)
                .build());
    }
}
