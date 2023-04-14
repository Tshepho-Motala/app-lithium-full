package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;

import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.VerificationStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.VERIFICATION_STATUS;

@Service
@Slf4j
public class VerificationStatusRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private LimitInternalSystemService limitInternalSystemService;

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            String verificationStatus = limitInternalSystemService.getVerificationStatusCode(context.getUser().getVerificationStatus());
            return validateRule(rule, verificationStatus);
        } catch (Status500LimitInternalSystemClientException e) {
            log.error("Unable to lookup user's verification status code while trying to"
                    + " auto-approve withdrawal for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return VERIFICATION_STATUS;
    }

    @Override
    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleInitValue = super.getAutoWithdrawalRuleInitValue(domain);
        List<VerificationStatusDto> statuses = limitInternalSystemService.getAllVerificationStatuses();

        ruleInitValue.setOptions(
                statuses.stream()
                        .map(status ->
                                AutoWithdrawalRuleField.Option.builder()
                                        .id(status.getCode())
                                        .name(status.getCode())
                                        .build())
                        .collect(Collectors.toList()));
        return ruleInitValue;
    }


}
