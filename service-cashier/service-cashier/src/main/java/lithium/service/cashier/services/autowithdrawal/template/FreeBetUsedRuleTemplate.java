package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.UserService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.FREE_BET_USED;

@Service
@Slf4j
public class FreeBetUsedRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountingClientService accountingClientService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            String accountType = "SPORTS_FREE_BET";
            String accountCode = "SPORTS_FREE_BET_SPORTSBOOK";
            String currency = userService.retrieveDomainFromDomainService(context.getUser().getDomain().getName()).getCurrency();
            boolean usedFreeBet = accountingClientService.isUsedFreeBet(context.guid(), currency, accountCode, accountType);
            return validateRule(rule, String.valueOf(usedFreeBet));
        } catch (Exception e) {
            log.error("Unable to get used free bets count for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return FREE_BET_USED;
    }

}
