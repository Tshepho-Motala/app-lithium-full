package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.UserService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.casino.client.objects.Granularity;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.PENDING_WITHDRAWALS_AMOUNT_IN_CENTS;

@Service
@Slf4j
public class PendingWithdrawalsAmountInCentsRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountingClientService accountingClientService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        final User user = context.getUser();
        try {
            String currency = userService.retrieveDomainFromDomainService(user.getDomain().getName()).getCurrency();
            long pendingWithdrawalsAmount = accountingClientService.findPendingWithdrawalsAmountInCentsByUserAndGranularityAndCurrency(
                    user.getDomain().getName(), user.guid(), Granularity.GRANULARITY_TOTAL.granularity(), currency);
            return validateRule(rule, String.valueOf(pendingWithdrawalsAmount));
        } catch (Exception e) {
            log.error("Unable to lookup user's pending withdrawals amount while trying to"
                    + " auto-approve withdrawal for " + user.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return PENDING_WITHDRAWALS_AMOUNT_IN_CENTS;
    }

}
