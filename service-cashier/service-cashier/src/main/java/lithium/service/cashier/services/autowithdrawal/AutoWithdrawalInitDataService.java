package lithium.service.cashier.services.autowithdrawal;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalInitData;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AutoWithdrawalInitDataService {
    @Autowired
    private MessageSource messages;
    @Autowired
    private AutoWithdrawalService autoWithdrawalService;

    public List<AutoWithdrawalRuleField> ruleTypes(Locale locale) {
        List<AutoWithdrawalRuleField> fields = Arrays
                .stream(AutoWithdrawalRuleType.values())
                .map(field -> AutoWithdrawalRuleField.builder()
                        .id(field.id())
                        .field(field.field())
                        .displayName(messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELD." + field.field(),
                                null, locale))
                        .build()
                )
                .collect(Collectors.toList());
        return fields;
    }

    public AutoWithdrawalInitData getInitData(String domain, AutoWithdrawalRuleType ruleType, Locale locale) throws Exception {
        AutoWithdrawalRuleTemplate template = autoWithdrawalService.getAutoWithdrawalRuleTemplateByType(ruleType)
                .orElseThrow(() -> new Exception("Related template not found"));
        return AutoWithdrawalInitData.builder()
                .ruleType(ruleType)
                .settings(updateWithTranslation(template.getAutoWithdrawalRuleSettings(), locale))
                .operator(getOperators(template.getOperators(), locale))
                .value(template.getAutoWithdrawalRuleInitValue(domain))
                .build();
    }

    private AutoWithdrawalRuleField getOperators(List<AutoWithdrawalRuleOperator> operators, Locale locale) {
        return AutoWithdrawalRuleField.builder()
                .options(operators.stream()
                        .map(o -> AutoWithdrawalRuleField.Option.builder()
                                .id(o.id().toString())
                                .name(messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.OPERATOR." + o.name(), null, locale))
                                .build())
                        .collect(Collectors.toList()))
                .field(messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.OPERATOR.PLACEHOLDER", null, locale))
                .type(AutoWithdrawalRuleFieldType.SINGLESELECT)
                .build();
    }

    private Map<Integer, AutoWithdrawalRuleField> updateWithTranslation(List<AutoWithdrawalRuleField> settingsNames, Locale locale) throws Exception {
        Map<Integer, AutoWithdrawalRuleField> settings = new HashMap<>();
        for (AutoWithdrawalRuleField settingField : settingsNames) {

            if (settingField.getDisplayName() != null && !settingField.getDisplayName().isEmpty()) {
                settingField.setDisplayName(messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELD.SETTINGS." + settingField.getKey() + ".LABEL", null, locale));
            }
            if (settingField.getDescription() != null && !settingField.getDescription().isEmpty()) {
                settingField.setDescription(messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELD.SETTINGS." + settingField.getKey() + ".DESCRIPTION", null, locale));
            }

            settings.put(settingField.getId(), settingField);
        }
        return settings;
    }


}
