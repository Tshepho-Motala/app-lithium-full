package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.WITHDRAWING_TO_LAST_USER_CARD;

@Service
@Slf4j
public class WithdrawingToLastUserCardRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            Transaction lastDeposit = getLastDeposit(context.guid());
            if (lastDeposit == null) {
                return ValidatedResult.failed();
            }
            ProcessorAccountType lastDepositProcessorAccountType = ProcessorAccountType.fromName(lastDeposit.getTransactionPaymentType().getPaymentType());
            if (!ProcessorAccountType.CARD.equals(lastDepositProcessorAccountType)) {
                return ValidatedResult.failed();
            }

            ProcessorUserCard paymentMethod = context.getTransaction().getPaymentMethod();
            ProcessorUserCard lastDepositPM = lastDeposit.getPaymentMethod();

            boolean isWithdrawingToLastUserDepositCard = lastDepositPM.getId() == paymentMethod.getId();
            return validateRule(rule, String.valueOf(isWithdrawingToLastUserDepositCard));
        } catch (Exception e) {
            log.error("Unable to lookup if payment method is withdrawing to last deposit card used while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return WITHDRAWING_TO_LAST_USER_CARD;
    }

}
