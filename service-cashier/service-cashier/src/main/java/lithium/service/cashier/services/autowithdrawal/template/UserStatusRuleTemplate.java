package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.RuleValue;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.USER_STATUS;

@Service
@Slf4j
public class UserStatusRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Autowired
    UserApiInternalClientService userApiInternalClientService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        return Optional.ofNullable(context.getUser().getStatus())
                .map(Status::getId)
                .map(String::valueOf)
                .map(status -> validateRule(rule, status))
                .orElse(ValidatedResult.failed());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return USER_STATUS;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
        List<Status> statusReasons = userApiInternalClientService.getAllUserStatuses();
        ruleInitValue.setOptions(
                statusReasons.stream()
                        .map(status -> AutoWithdrawalRuleField.Option.builder()
                                .id(Long.toString(status.getId()))
                                .name(status.getName())
                                .build())
                        .collect(Collectors.toList()));
        return ruleInitValue;
    }

    @Override
    public List<RuleValue> resolveValue(String rawValue) {
        List<RuleValue> ruleValue = new ArrayList<>();
        Map<Long, Status> availableStatuses = availableStatuses();
        for (String value : rawValue.split(",")) {
            String description = ofNullable(availableStatuses.get(Long.parseLong(value)))
                    .map(Status::getName)
                    .orElse(value);
            ruleValue.add(new RuleValue(description, value));
        }
        return ruleValue;
    }

    private Map<Long, Status> availableStatuses() {
        try {
            return userApiInternalClientService.getAllUserStatuses()
                    .stream()
                    .collect(Collectors.toMap(Status::getId, Function.identity()));
        } catch (Exception e) {
            log.warn("Can't retrieve available user statuses due ", e);
            return new HashMap<>();
        }
    }
}
