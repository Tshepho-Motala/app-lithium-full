package lithium.service.accounting.enums;

import lombok.Getter;

@Getter
public enum TransactionTypeCode {

    OPERATOR_MIGRATION_DEBIT("OPERATOR_MIGRATION_DEBIT"),
    PLAYER_BALANCE_OPERATOR_MIGRATION("PLAYER_BALANCE_OPERATOR_MIGRATION"),
    OPERATOR_MIGRATION_CREDIT("OPERATOR_MIGRATION_CREDIT");

    private final String name;

    TransactionTypeCode(String name) {
        this.name = name;
    }

    public static TransactionTypeCode fromName(String name) {

        for (TransactionTypeCode s: TransactionTypeCode.values()) {
            if (s.name.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

}
