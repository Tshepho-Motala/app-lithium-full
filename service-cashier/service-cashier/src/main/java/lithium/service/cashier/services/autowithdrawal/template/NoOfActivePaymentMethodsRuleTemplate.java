package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.isNull;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.NO_OF_ACTIVE_PAYMENT_METHODS;

@Service
public class NoOfActivePaymentMethodsRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private ProcessorAccountService processorAccountService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return Optional.ofNullable(processorAccountService.getActiveProcessorAccountsCount(context.getUser().guid()))
                .map(String::valueOf)
                .map(activePaymentsCount -> validateRule(rule, activePaymentsCount))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return NO_OF_ACTIVE_PAYMENT_METHODS;
    }

}
