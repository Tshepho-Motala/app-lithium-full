package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.RuleValue;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.user.client.objects.StatusReason;
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
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.USER_STATUS_REASON;

@Service
@Slf4j
public class UserStatusReasonRuleTemplate extends AutoWithdrawalRuleTemplate {

    @Autowired
    UserApiInternalClientService userApiInternalClientService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        Long statusReasonId = Optional.ofNullable(context.getUser().getStatusReason())
                .map(StatusReason::getId)
                .orElse(0L);
        return validateRule(rule, statusReasonId.toString());
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return USER_STATUS_REASON;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
        List<StatusReason> statusReasons = userApiInternalClientService.getAllStatusReasons();
        ruleInitValue.setOptions(
                statusReasons.stream()
                        .map(reason -> AutoWithdrawalRuleField.Option.builder()
                                .id(Long.toString(reason.getId()))
                                .name(reason.getName())
                                .build())
                        .collect(Collectors.toList()));
        return ruleInitValue;
    }

    @Override
    public List<RuleValue> resolveValue(String rawValue) {
        List<RuleValue> ruleValue = new ArrayList<>();
        Map<Long, StatusReason> availableStatuses = availableStatusReasons();
        for (String value : rawValue.split(",")) {
            String description = ofNullable(availableStatuses.get(Long.parseLong(value)))
                    .map(StatusReason::getName)
                    .orElse(value);
            ruleValue.add(new RuleValue(description, value));
        }
        return ruleValue;
    }

    private Map<Long, StatusReason> availableStatusReasons() {
        try {
            return userApiInternalClientService.getAllStatusReasons()
                    .stream()
                    .collect(Collectors.toMap(StatusReason::getId, Function.identity()));
        } catch (Exception e) {
            log.warn("Can't retrieve available user status reasons due ", e);
            return new HashMap<>();
        }
    }
}
