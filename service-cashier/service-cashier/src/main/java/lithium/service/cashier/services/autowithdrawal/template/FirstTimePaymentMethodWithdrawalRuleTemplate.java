package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.FIRST_TIME_PAYMENT_METHOD_WITHDRAWAL;

@Service
@Slf4j
public class FirstTimePaymentMethodWithdrawalRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            Long successWithdrawalTransactionsCount = getTransactionService().countAllSuccessWithdrawalTransactionsByUserGuidAndDomainMethod(context.guid(), context.getDomainMethod());
            return validateRule(rule, String.valueOf(successWithdrawalTransactionsCount == 0));
        } catch (Exception e) {
            log.error("Unable to lookup first time payment method withdrawal while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return FIRST_TIME_PAYMENT_METHOD_WITHDRAWAL;
    }

}
