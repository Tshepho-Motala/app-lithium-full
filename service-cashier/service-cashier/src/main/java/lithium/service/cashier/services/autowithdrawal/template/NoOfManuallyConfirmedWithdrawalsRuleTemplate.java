package lithium.service.cashier.services.autowithdrawal.template;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.enums.Type;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.stats.client.service.StatsClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.NO_OF_MANUALLY_CONFIRMED_WITHDRAWALS;

@Service
@Slf4j
public class NoOfManuallyConfirmedWithdrawalsRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private StatsClientService statsClientService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            long noOfManuallyConfirmedWithdrawals = statsClientService.getAllTimeStatCountForUser(context.guid(),
                    Type.CASHIER.type(), Event.MANUAL_APPROVED_WITHDRAWAL.event());
            log.debug("noOfManuallyConfirmedWithdrawals :: " + noOfManuallyConfirmedWithdrawals);
            return validateRule(rule, String.valueOf(noOfManuallyConfirmedWithdrawals));
        } catch (Status513StatsServiceUnavailableException | Status500InternalServerErrorException e) {
            log.error("Unable to lookup manually approved withdrawal count while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage());
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return NO_OF_MANUALLY_CONFIRMED_WITHDRAWALS;
    }

}
