package lithium.service.accounting.enums;

import lombok.Getter;

@Getter
public enum IngestionAccountCode {

    OPERATOR_MIGRATION_OPENING_DEBIT("OPERATOR_MIGRATION_OPENING_DEBIT", TransactionTypeCode.OPERATOR_MIGRATION_DEBIT),
    PLAYER_BALANCE("PLAYER_BALANCE", TransactionTypeCode.OPERATOR_MIGRATION_DEBIT),
    OPERATOR_MIGRATION_OPENING_CREDIT("OPERATOR_MIGRATION_OPENING_CREDIT", TransactionTypeCode.OPERATOR_MIGRATION_CREDIT),
    PLAYER_BALANCE_OPERATOR_MIGRATION("PLAYER_BALANCE_OPERATOR_MIGRATION", TransactionTypeCode.PLAYER_BALANCE_OPERATOR_MIGRATION);

    private final String name;
    private final TransactionTypeCode transactionTypeCode;

    IngestionAccountCode(String name, TransactionTypeCode transactionTypeCode) {
        this.name = name;
        this.transactionTypeCode = transactionTypeCode;
    }

    public static IngestionAccountCode fromName(String name) {
        for (IngestionAccountCode s : IngestionAccountCode.values()) {
            if (s.name.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
    public static IngestionAccountCode fromTransactionTypeCode(String transactionTypeCode) {
        for (IngestionAccountCode s : IngestionAccountCode.values()) {
            if (s.transactionTypeCode.getName().equalsIgnoreCase(transactionTypeCode)) {
                return s;
            }
        }
        return null;
    }
}
