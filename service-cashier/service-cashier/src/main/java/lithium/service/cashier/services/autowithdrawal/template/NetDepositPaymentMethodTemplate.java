package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.NET_DEPOSIT_AMOUNT_IN_CENTS_PM;

@Service
@Slf4j
public class NetDepositPaymentMethodTemplate extends AutoWithdrawalRuleTemplate {
    @Override
    public AutoWithdrawalRuleType getType() {
        return NET_DEPOSIT_AMOUNT_IN_CENTS_PM;
    }

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        ProcessorUserCard paymentMethod = context.getTransaction().getPaymentMethod();
        long debitSummaryAmount = getTransactionService().getSummarySuccessDepositAmountForUserByPaymentMethod(context.getUser().guid(), paymentMethod);
        return validateRule(rule, String.valueOf(debitSummaryAmount));
    }
}
