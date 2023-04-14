package lithium.service.libraryvbmigration.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MigrationType {
    USER_MIGRATION("historic_user_ingestions", "USER_MIGRATION"),
    TRANSACTION_MIGRATION("AccountingLimitsTable", "TRANSACTION_MIGRATION"),
    OPENING_BALANCE_PHASE1_MIGRATION("openingBalancesTest", "OPENING_BALANCE_PHASE1_MIGRATION"),
    OPENING_BALANCE_PHASE2_MIGRATION("openingBalancesTest_2", "OPENING_BALANCE_PHASE2_MIGRATION"),
    CASINO_GAMES_MIGRATION("casinoSnapshotDistinctGame", "CASINO_GAMES_MIGRATION"),
    CASINO_BETS_MIGRATION("casinoSnapshot_table", "CASINO_BETS_MIGRATION"),
    CASHIER_PAYMENT_METHODS_MIGRATION("CashierPaymentMethods_table", "CASHIER_PAYMENT_METHODS_MIGRATION"),
    CASHIER_TRANSACTIONS_MIGRATION("CashierTransactions_table", "CASHIER_TRANSACTIONS_MIGRATION"),
    CASHIER_UPO_INGESTION("CashierUsersForUPOIngestion_table", "CASHIER_UPO_INGESTION"),
    ACCOUNT_NOTES_MIGRATION_PREP("CustomerNotes_distinct", "ACCOUNT_NOTES_MIGRATION_PREP"),

    ACCOUNT_NOTES_MIGRATION("CustomerNotes", "ACCOUNT_NOTES_MIGRATION"),
    PLAYER_LIMIT_PREFERENCES_MIGRATION("limits", "PLAYER_LIMIT_PREFERENCES_MIGRATION"),
    REALITY_CHECK_MIGRATION("CustomerAdditionalDataSM", "REALITY_CHECK_MIGRATION"),
    SELF_EXCLUSION_COOL_OFF_PREFERENCE("FakeSelfExclusionTimeout", "SELF_EXCLUSION_COOL_OFF_PREFERENCE");
    @Setter
    @Accessors(fluent = true)
    @Getter
    private String table;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private String type;

    @JsonCreator
    public static MigrationType fromType(String type) {
        for (MigrationType m : MigrationType.values()) {
            if (m.type.equalsIgnoreCase(type)) {
                return m;
            }
        }
        return null;
    }

    @JsonCreator
    public static MigrationType fromTable(String table) {
        for (MigrationType m : MigrationType.values()) {
            if (m.table.equalsIgnoreCase(table)) {
                return m;
            }
        }
        return null;
    }
}
