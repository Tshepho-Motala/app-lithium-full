package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.IS_WITHDRAWING_TO_LAST_DEPOSIT_METHOD_USED;

@Service
@Slf4j
public class IsWithdrawingToLastDepositMethodUsedRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            Transaction lastDeposit = getLastDeposit(context.guid());
            if (lastDeposit == null) {
                return ValidatedResult.failed();
            }
            final String methodCode = context.getDomainMethod().getMethod().getCode();
            final String lstDepositMethodCode = lastDeposit.getDomainMethod().getMethod().getCode();
            boolean isWithdrawingToLastDepositMethodUsed = methodCode.equals(lstDepositMethodCode);
            return validateRule(rule, String.valueOf(isWithdrawingToLastDepositMethodUsed));
        } catch (Exception e) {
            log.error("Unable to lookup if payment method is withdrawing to last deposit method used while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return IS_WITHDRAWING_TO_LAST_DEPOSIT_METHOD_USED;
    }

}
