package lithium.service.cashier.client.objects.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType.BOOLEAN;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType.LONG;
import static lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType.MULTISELECT;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AutoWithdrawalRuleType {
    WITHDRAW_AMOUNT_CENTS(0, "WITHDRAW_AMOUNT_CENTS", LONG),
    DAYS_SINCE_REGISTRATION(1, "DAYS_SINCE_REGISTRATION", LONG),
    NO_OF_MANUALLY_CONFIRMED_WITHDRAWALS(2, "NO_OF_MANUALLY_CONFIRMED_WITHDRAWALS", LONG),
    NO_OF_AUTO_APPROVED_WITHDRAWALS(3, "NO_OF_AUTO_APPROVED_WITHDRAWALS", LONG),
    DAYS_SINCE_LAST_DEPOSIT(4, "DAYS_SINCE_LAST_DEPOSIT", LONG),
    LAST_DEPOSIT_AMOUNT_CENTS(5, "LAST_DEPOSIT_AMOUNT_CENTS", LONG),
    PAYMENT_METHOD(6, "PAYMENT_METHOD", MULTISELECT),
    VERIFICATION_STATUS(7, "VERIFICATION_STATUS", MULTISELECT),
    NO_OF_ACTIVE_PAYMENT_METHODS(8, "NO_OF_ACTIVE_PAYMENT_METHODS", LONG),
    DAYS_SINCE_FIRST_DEPOSIT(9, "DAYS_SINCE_FIRST_DEPOSIT", LONG),
    LT_WITHDRAWALS_AMOUNT_IN_CENTS(10, "LT_WITHDRAWALS_AMOUNT_IN_CENTS", LONG),
    PLAYER_TAGS(11, "PLAYER_TAGS", MULTISELECT),
    FIRST_TIME_PAYMENT_METHOD_WITHDRAWAL(12, "FIRST_TIME_PAYMENT_METHOD_WITHDRAWAL", BOOLEAN),
    IS_VERIFIED_PAYMENT_METHOD(13, "IS_VERIFIED_PAYMENT_METHOD", BOOLEAN),
    IS_CONTRA_ACCOUNT_PAYMENT_METHOD(14, "IS_CONTRA_ACCOUNT_PAYMENT_METHOD", BOOLEAN),
    PENDING_WITHDRAWALS_AMOUNT_IN_CENTS(15, "PENDING_WITHDRAWALS_AMOUNT_IN_CENTS", LONG),
    IS_WITHDRAWING_TO_LAST_DEPOSIT_METHOD_USED(16, "IS_WITHDRAWING_TO_LAST_DEPOSIT_METHOD_USED", BOOLEAN),
    WITHDRAWING_TO_LAST_USER_CARD(18, "WITHDRAWING_TO_LAST_USER_CARD", BOOLEAN),
    FREE_BET_USED(19, "FREE_BET_USED", BOOLEAN),
    CASINO_BETS_COVERAGE(20, "CASINO_BETS_COVERAGE", LONG),
    LAST_DEPOSIT_PAYMENT_METHOD(21, "LAST_DEPOSIT_PAYMENT_METHOD", MULTISELECT),
    LT_DEPOSIT_AMOUNT_IN_CENTS(22, "LT_DEPOSIT_AMOUNT_IN_CENTS", LONG),
    USER_STATUS(23, "USER_STATUS", MULTISELECT),
    USER_STATUS_REASON(24, "USER_STATUS_REASON", MULTISELECT),
    WD_AMOUNT_LESS_NET_DEPOSIT_PM(25, "WD_AMOUNT_LESS_NET_DEPOSIT_PM", BOOLEAN),
    NET_DEPOSIT_AMOUNT_IN_CENTS_PM(26, "NET_DEPOSIT_AMOUNT_IN_CENTS_PM", LONG),
    MANUALLY_APPROVED_WDS(27, "MANUALLY_APPROVED_WDS", LONG),
    AUTO_APPROVED_WDS(28, "AUTO_APPROVED_WDS", LONG),
    SYSTEM_WD_OVER_BALANCE_LIMIT(29, "SYSTEM_WD_OVER_BALANCE_LIMIT", BOOLEAN),
    MANUALLY_APPROVED_WDS_LIMITED(30, "MANUALLY_APPROVED_WDS_LIMITED", LONG),
    AUTO_APPROVED_WDS_LIMITED(31, "AUTO_APPROVED_WDS_LIMITED", LONG);


    @Setter
    @Accessors(fluent = true)
    private Integer id;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private String field;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private AutoWithdrawalRuleFieldType valueType;

    public static AutoWithdrawalRuleType fromField(String field) {
        for (AutoWithdrawalRuleType a : AutoWithdrawalRuleType.values()) {
            if (a.field.equalsIgnoreCase(field)) {
                return a;
            }
        }
        return null;
    }

    @JsonCreator
    public static AutoWithdrawalRuleType fromId(Integer id) {
        for (AutoWithdrawalRuleType o : AutoWithdrawalRuleType.values()) {
            if (o.id.equals(id)) {
                return o;
            }
        }
        return null;
    }

    @JsonValue
    public Integer id() {
        return id;
    }
}
