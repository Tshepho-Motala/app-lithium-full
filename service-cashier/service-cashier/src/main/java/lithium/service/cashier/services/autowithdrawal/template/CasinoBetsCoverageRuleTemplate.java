package lithium.service.cashier.services.autowithdrawal.template;

import lithium.service.Response;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.objects.AccountCode;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalRuleTemplate;
import lithium.service.cashier.services.autowithdrawal.RuleValidateContext;
import lithium.service.cashier.services.autowithdrawal.ValidatedResult;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType.CASINO_BETS_COVERAGE;

@Service
@Slf4j
public class CasinoBetsCoverageRuleTemplate extends AutoWithdrawalRuleTemplate {
    @Autowired
    private AccountingClientService accountingClientService;
    @Autowired
    private LithiumServiceClientFactory services;

    final String LIST_OF_ACCOUNT_CODES = "LIST_OF_ACCOUNT_CODES";

    @Override
    public ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context) {
        try {
            Transaction lastDeposit = getLastDeposit(context.guid());
            if (lastDeposit == null || lastDeposit.getAmountCents() == 0l) {
                return ValidatedResult.failed();
            }
            if (!rule.getSettingValueByKey(LIST_OF_ACCOUNT_CODES).isPresent()) {
                log.warn("Missing expected settings (rule: " + rule.getId() + ", guid: " + context.guid() + ")");
                return ValidatedResult.failed();
            }

            String setting = rule.getSettingValueByKey(LIST_OF_ACCOUNT_CODES).get();
            final List<String> accountCodes = Arrays.stream(setting.split(","))
                    .collect(Collectors.toList());
            Long combinedTurnover = accountingClientService.getUserTurnoverFrom(context.guid(), lastDeposit.getCreatedOn(),
                    accountCodes, "GRANULARITY_DAY");
            log.debug("User turnover parameters : guid=[" + context.guid() + "], lastDeposit date=[" + lastDeposit.getCreatedOn() + "]. Combined Turnover = [" + combinedTurnover + "]");

            return validateRule(rule, String.valueOf(combinedTurnover * 100 / lastDeposit.getAmountCents()));
        } catch (Exception e) {
            log.error("Unable to get used Sports/Casino bets last deposit coverage for " + context.guid() + " | " + e.getMessage(), e);
            return ValidatedResult.failed();
        }
    }

    @Override
    public AutoWithdrawalRuleType getType() {
        return CASINO_BETS_COVERAGE;
    }

    @Override
    public List<AutoWithdrawalRuleField> getAutoWithdrawalRuleSettings() throws Exception {
        return Arrays.asList(AutoWithdrawalRuleField.builder()
                .id(1)
                .key(LIST_OF_ACCOUNT_CODES)
                .type(AutoWithdrawalRuleFieldType.MULTISELECT)
                .options(getListOfAccountCodes())
                .build());
    }

    private List<AutoWithdrawalRuleField.Option> getListOfAccountCodes() throws Exception {
        try {
            AdminTransactionsClient client = services.target(AdminTransactionsClient.class, "service-accounting-provider-internal", true);
            Response<List<AccountCode>> response = client.getAllAccountCodes();
            return response.getData().stream()
                    .map(dm -> AutoWithdrawalRuleField.Option.builder()
                            .id(dm.getCode())
                            .name(dm.getCode())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Failed to get account codes. Exception: ", ex.getMessage(), ex);
            throw ex;
        }
    }


}
