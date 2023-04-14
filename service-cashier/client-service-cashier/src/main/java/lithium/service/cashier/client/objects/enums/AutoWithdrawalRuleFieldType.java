package lithium.service.cashier.client.objects.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.BETWEEN;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.EQUALS;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.GREATER_THAN;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.GREATER_THAN_OR_EQUALS;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.IN;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.LESS_THAN;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.LESS_THAN_OR_EQUALS;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.NOT_IN;

@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AutoWithdrawalRuleFieldType {
    LONG(BETWEEN, EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS),
    STRING(EQUALS),
    BOOLEAN(EQUALS),
    SINGLESELECT(EQUALS),
    MULTISELECT(IN, NOT_IN);

    private AutoWithdrawalRuleFieldType(AutoWithdrawalRuleOperator... supportedOperators) {
        this.supportedOperators = Arrays.asList(supportedOperators);
    }

    @Getter
    private List<AutoWithdrawalRuleOperator> supportedOperators;

    public static AutoWithdrawalRuleFieldType fromName(String name) {
        for (AutoWithdrawalRuleFieldType a : AutoWithdrawalRuleFieldType.values()) {
            if (a.name().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }
}
