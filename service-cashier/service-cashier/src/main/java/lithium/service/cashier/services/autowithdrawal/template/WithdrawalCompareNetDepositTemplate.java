package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.WD_AMOUNT_LESS_NET_DEPOSIT_PM;

@Service
@Slf4j
public class WithdrawalCompareNetDepositTemplate extends AutoWithdrawalRuleTemplate {
    @Override
    public AutoWithdrawalRuleType getType() {
        return WD_AMOUNT_LESS_NET_DEPOSIT_PM;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        ProcessorUserCard paymentMethod = context.getTransaction().getPaymentMethod();
        long withdrawAmount = context.getTransaction().getAmountCents();
        long debitSummaryAmount = getTransactionService().getSummarySuccessDepositAmountForUserByPaymentMethod(context.getUser().guid(), paymentMethod);
        return validateRule(rule, String.valueOf(withdrawAmount < debitSummaryAmount));
    }
}
