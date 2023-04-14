package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.PAYMENT_METHOD;

@Service
@Slf4j
public class PaymentMethodRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return validateRule(rule, context.getDomainMethod().getMethod().getCode());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return PAYMENT_METHOD;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
        setPaymentMethods(ruleInitValue, domain, ProcessorType.WITHDRAW);
        return ruleInitValue;
    }


}
