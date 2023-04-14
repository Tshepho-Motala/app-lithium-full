package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.IS_VERIFIED_PAYMENT_METHOD;

@Service
@Slf4j
public class IsVerifiedPaymentMethodRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            ProcessorUserCard paymentMethod = context.getTransaction().getPaymentMethod();
            if (isNull(paymentMethod)) {
                return ValidatedResult.failed();
            }
            return validateRule(rule, String.valueOf(paymentMethod.getVerified()));
        } catch (Exception e) {
            log.error("Unable to lookup if payment method is verified while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return IS_VERIFIED_PAYMENT_METHOD;
    }

}
