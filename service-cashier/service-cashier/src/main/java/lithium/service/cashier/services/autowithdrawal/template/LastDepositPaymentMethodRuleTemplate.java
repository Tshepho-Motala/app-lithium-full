package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.LAST_DEPOSIT_PAYMENT_METHOD;

@Service
@Slf4j
public class LastDepositPaymentMethodRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return Optional.ofNullable(getLastDeposit(context.guid()))
                .map(transaction -> transaction.getDomainMethod().getMethod().getCode())
                .map(code -> validateRule(rule, code))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return LAST_DEPOSIT_PAYMENT_METHOD;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
        setPaymentMethods(ruleInitValue, domain, ProcessorType.DEPOSIT);
        return ruleInitValue;
    }


}
