package lithium.service.cashier.services.autowithdrawal;

import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.RuleValue;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class AutoWithdrawalRuleTemplate {
    @Autowired
    private DomainMethodService domainMethodService;
    @Autowired
    private TransactionService transactionService;

    public Transaction getLastDeposit(String guid) {
        return transactionService.findLastTransaction(guid, TransactionType.DEPOSIT, DoMachineState.SUCCESS.name());
    }

    public abstract ValidatedResult validate(AutoWithdrawalRule rule, RuleValidateContext context);

    public abstract AutoWithdrawalRuleType getType();

    public List<RuleValue> resolveValue(String rawValue) {
        List<RuleValue> ruleValue = new ArrayList<>();
        for (String value : rawValue.split(",")) {
            ruleValue.add(new RuleValue(value, value));
        }
        return ruleValue;
    }

    public List<AutoWithdrawalRuleOperator> getOperators() {
        return getType().valueType().getSupportedOperators();
    }

    public AutoWithdrawalRuleField getAutoWithdrawalRuleInitValue(String domain) throws Exception {
        final AutoWithdrawalRuleField ruleField = AutoWithdrawalRuleField.builder()
                .type(getType().valueType())
                .build();
        if (getType().valueType().equals(AutoWithdrawalRuleFieldType.BOOLEAN)) {
            ruleField.setOptions(Arrays.asList(
                    AutoWithdrawalRuleField.Option.builder()
                            .id("Yes").name("Yes")
                            .build(),
                    AutoWithdrawalRuleField.Option.builder()
                            .id("No").name("No")
                            .build()));
        }
        return ruleField;
    }

    public List<AutoWithdrawalRuleField> getAutoWithdrawalRuleSettings() throws Exception {
        return Collections.emptyList();
    }

    public ValidatedResult validateRule(AutoWithdrawalRule rule, String strValue) {
        boolean result = false;

        switch (rule.getField().valueType()) {
            case LONG: {
                result = validateLong(rule, strValue);
                break;
            }
            case SINGLESELECT:
            case STRING:
                result = strValue.equalsIgnoreCase(rule.getValue());
                break;
            case MULTISELECT: {
                String[] values = rule.getValue().split(",");
                result = validateMultiselect(rule.getOperator(), strValue, values);
                break;
            }
            case BOOLEAN: {
                String yesOrNo = Boolean.parseBoolean(strValue) ? "Yes" : "No";
                result = yesOrNo.equals(rule.getValue());
                break;
            }
        }

        return ValidatedResult.of(result, strValue);
    }

    private boolean validateLong(AutoWithdrawalRule rule, String strValue) {
        try {
            long value = Long.parseLong(strValue);
            long ruleValue = Long.parseLong(rule.getValue());
            Long ruleValue2 = null;
            if (rule.getValue2() != null) {
                ruleValue2 = Long.parseLong(rule.getValue2());
            }
            return validate(rule.getOperator(), value, ruleValue, ruleValue2);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validate(AutoWithdrawalRuleOperator operator, long value, long ruleValue, Long ruleValue2) {
        boolean result = false;
        switch (operator) {
            case BETWEEN:
                if (ruleValue2 == null) return false;
                result = (value >= ruleValue && value <= ruleValue2.longValue());
                break;
            case EQUALS:
                result = (value == ruleValue);
                break;
            case GREATER_THAN:
                result = (value > ruleValue);
                break;
            case GREATER_THAN_OR_EQUALS:
                result = (value >= ruleValue);
                break;
            case LESS_THAN:
                result = (value < ruleValue);
                break;
            case LESS_THAN_OR_EQUALS:
                result = (value <= ruleValue);
                break;
            default:
                return false;
        }
        return result;
    }

    public boolean validateMultiselect(AutoWithdrawalRuleOperator operator, String strValue, String[] ruleValues) {
        boolean result = false;
        switch (operator) {
            case IN:
                result = Arrays.stream(ruleValues).anyMatch(value -> value.contentEquals(strValue));
                break;
            case NOT_IN:
                result = Arrays.stream(ruleValues).noneMatch(value -> value.contentEquals(strValue));
                break;
            default:
                return false;
        }
        return result;
    }

    public void setPaymentMethods(AutoWithdrawalRuleField field, String domain, ProcessorType type) {
        field.setOptions(domainMethodService.list(domain, type).stream()
                .map(dm ->
                        AutoWithdrawalRuleField.Option.builder()
                                .id(dm.getMethod().getCode())
                                .name(dm.getMethod().getCode())
                                .build())
                .distinct()
                .collect(Collectors.toList()));
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public static Long getMaxConsecutiveAutoApprovedTransactionsCount(List<Transaction> transactions) {
        return getMaxConsecutiveCount(transactions, Transaction::isAutoApproved);
    }

    public static Long getMaxConsecutiveManuallyApprovedTransactionsCount(List<Transaction> transactions) {
        return getMaxConsecutiveCount(transactions, Predicate.not(Transaction::isAutoApproved));
    }

    private static <T> Long getMaxConsecutiveCount(List<T> objects, Predicate<T> filter) {
        long maxCount = 0;
        long count = 0;
        for (T object : objects) {
            if (filter.test(object)) {
                count++;
            } else {
                if (count > maxCount) {
                    maxCount = count;
                }
                count = 0;
            }
        }
        if (count > maxCount) {
            maxCount = count;
        }
        return maxCount;
    }
}

