package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.UserService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.PLAYER_TAGS;

@Service
@Slf4j
public class PlayerTagsRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private UserService userService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            List<String> userPlayerTags = userService.findUserPlayerTagNames(context.getUser().getId());
            if (rule.getOperator() == AutoWithdrawalRuleOperator.IN) {
                for (String userPlayerTag : userPlayerTags) {
                    if (validateRule(rule, userPlayerTag).isValidated()) {
                        return ValidatedResult.of(true, userPlayerTag);
                    }
                }
                return ValidatedResult.failed();
            } else if (rule.getOperator() == AutoWithdrawalRuleOperator.NOT_IN) {
                for (String userPlayerTag : userPlayerTags) {
                    if (!validateRule(rule, userPlayerTag).isValidated()) {
                        return ValidatedResult.failed();
                    }
                }
                String value = userPlayerTags.stream().collect(Collectors.joining(","));
                return ValidatedResult.of(true, value);
            }
        } catch (Exception e) {
            log.error("Unable to lookup player tags while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
        }
        return ValidatedResult.failed();
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return PLAYER_TAGS;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        try {
            final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
            ruleInitValue.setOptions(
                    userService.getDomainUserCategories(domain).stream()
                            .map(uc ->
                                    AutoWithdrawalRuleField.Option.builder()
                                            .id(uc.getName())
                                            .name(uc.getName())
                                            .build())
                            .collect(Collectors.toList()));
            return ruleInitValue;
        } catch (Exception ex) {
            log.error("Failed to get domain categories codes. Exception: ", ex.getMessage(), ex);
            throw ex;
        }
    }
}
