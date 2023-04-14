package lithium.service.cashier.services.autowithdrawal;

import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleDto;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleSetDto;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleSettingsDto;
import lithium.service.cashier.client.objects.Domain;
import lithium.service.cashier.client.objects.RuleValue;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSettings;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
@AllArgsConstructor
public class AutoWithdrawalDtoConvertService {
    private final UserApiInternalClientService userApiInternalClientService;
    private final AutoWithdrawalService autoWithdrawalService;

    public AutoWithdrawalRuleSetDto ruleSetToDto(AutoWithdrawalRuleSet entity) {
        if (isNull(entity)) {
            return null;
        }
        return AutoWithdrawalRuleSetDto.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .domain(domainToDto(entity.getDomain()))
                .name(entity.getName())
                .enabled(entity.isEnabled())
                .deleted(entity.isDeleted())
                .lastUpdated(entity.getLastUpdated())
                .lastUpdatedBy(userApiInternalClientService.getUserName(entity.getLastUpdatedBy()))
                .delay(entity.getDelay())
                .delayedStart(entity.isDelayedStart())
                .rules(entity.getRules().stream().map(this::ruleToDto).collect(Collectors.toList()))
                .build();
    }

    public AutoWithdrawalRuleSet ruleSetToEntity(AutoWithdrawalRuleSetDto ruleSetDto) {
        if (isNull(ruleSetDto)) {
            return null;
        }
        return AutoWithdrawalRuleSet.builder()
                .id(ruleSetDto.getId())
                .version(ruleSetDto.getVersion())
                .name(ruleSetDto.getName())
                .enabled(ruleSetDto.isEnabled())
                .deleted(ruleSetDto.isDeleted())
                .lastUpdated(ruleSetDto.getLastUpdated())
                .lastUpdatedBy(ruleSetDto.getLastUpdatedBy())
                .delay(ruleSetDto.getDelay())
                .delayedStart(ruleSetDto.isDelayedStart())
                .rules(ruleSetDto.getRules().stream().map(this::ruleToEntity).collect(Collectors.toList()))
                .build();
    }

    public AutoWithdrawalRule ruleToEntity(AutoWithdrawalRuleDto ruleDto) {
        if (isNull(ruleDto)) {
            return null;
        }
        return AutoWithdrawalRule.builder()
                .id(ruleDto.getId())
                .version(ruleDto.getVersion())
                .enabled(ruleDto.isEnabled())
                .deleted(ruleDto.isDeleted())
                .field(ruleDto.getField())
                .settings(ruleDto.getSettings().stream().map(this::ruleSettingsToEntity).collect(Collectors.toList()))
                .operator(ruleDto.getOperator())
                .value(ruleDto.getValue().stream().map(RuleValue::getValue).collect(Collectors.joining(",")))
                .value2(ruleDto.getValue2())
                .build();
    }

    public AutoWithdrawalRuleSettings ruleSettingsToEntity(AutoWithdrawalRuleSettingsDto settingsDto) {
        if (isNull(settingsDto)) {
            return null;
        }
        return AutoWithdrawalRuleSettings.builder()
                .id(settingsDto.getId())
                .version(settingsDto.getVersion())
                .key(settingsDto.getKey())
                .value(settingsDto.getValue())
                .build();
    }

    public AutoWithdrawalRuleDto ruleToDto(AutoWithdrawalRule rule) {
        if (isNull(rule)) {
            return null;
        }
        return autoWithdrawalService.getAutoWithdrawalRuleTemplateByType(rule.getField())
                .map(template -> {
                    return AutoWithdrawalRuleDto.builder()
                            .id(rule.getId())
                            .version(rule.getVersion())
                            .rulesetId(rule.getRuleset().getId())
                            .enabled(rule.isEnabled())
                            .deleted(rule.isDeleted())
                            .field(rule.getField())
                            .settings(rule.getSettings().stream().map(this::ruleSettingsToDto).collect(Collectors.toList()))
                            .operator(rule.getOperator())
                            .value(template.resolveValue(rule.getValue()))
                            .value2(rule.getValue2())
                            .build();
                }).orElse(null);
    }

    public AutoWithdrawalRuleSettingsDto ruleSettingsToDto(AutoWithdrawalRuleSettings settings) {
        if (isNull(settings)) {
            return null;
        }
        return AutoWithdrawalRuleSettingsDto.builder()
                .id(settings.getId())
                .version(settings.getVersion())
                .key(settings.getKey())
                .value(settings.getValue())
                .build();
    }

    public Domain domainToDto(lithium.service.cashier.data.entities.Domain domain) {
        if (isNull(domain)) {
            return null;
        }
        return Domain.builder()
                .id(domain.getId())
                .name(domain.getName())
                .version(domain.getVersion())
                .build();
    }
}