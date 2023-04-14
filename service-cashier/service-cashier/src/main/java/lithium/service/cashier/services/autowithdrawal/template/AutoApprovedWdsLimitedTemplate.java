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

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.AUTO_APPROVED_WDS_LIMITED;

@Service
@Slf4j
public class AutoApprovedWdsLimitedTemplate extends AutoWithdrawalRuleTemplate {

    private static final String NUMBER_OF_WITHDRAWAL = "NUMBER_OF_WITHDRAWAL";
    @Override
    public AutoWithdrawalRuleType getType() {
        return AUTO_APPROVED_WDS_LIMITED;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        String setting = rule.getSettingValueByKey(NUMBER_OF_WITHDRAWAL).get();
        PageRequest pageRequest = PageRequest.of(0, Integer.valueOf(setting));
        Page<Transaction> transactions = getTransactionService().findByUserGuidAndTransactionTypeAndStatusCodeInOrderByIdDesc (context.guid(), TransactionType.WITHDRAWAL, DoMachineState.getFinalStateCodes(), pageRequest);
        Long maxAutoApprovedWdsCount = getMaxConsecutiveAutoApprovedTransactionsCount(transactions.getContent());
        log.debug("Max auto approved count = " + maxAutoApprovedWdsCount + " from " + transactions.getContent().size() + " withdrawals");
        return validateRule(rule, String.valueOf(maxAutoApprovedWdsCount));
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


