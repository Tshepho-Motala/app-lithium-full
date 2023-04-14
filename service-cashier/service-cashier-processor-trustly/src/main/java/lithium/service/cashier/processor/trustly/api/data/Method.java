package lithium.service.cashier.processor.trustly.api.data;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum Method {
    @JsonProperty("Deposit")
    DEPOSIT("Deposit"),

    @JsonProperty("Withdraw")
    WITHDRAW("Withdraw"),

    @JsonProperty("ApproveWithdrawal")
    APPROVE_WITHDRAWAL("ApproveWithdrawal"),

    @JsonProperty("DenyWithdrawal")
    DENY_WITHDRAWAL("DenyWithdrawal"),

    @JsonProperty("AccountLedger")
    ACCOUNT_LEDGER("AccountLedger"),

    @JsonProperty("ViewAutomaticSettlementDetailsCSV")
    VIEW_AUTOMATIC_SETTLEMENT_DETAILS_CSV("ViewAutomaticSettlementDetailsCSV"),

    @JsonProperty("Balance")
    BALANCE("Balance"),

    @JsonProperty("GetWithdrawals")
    GET_WITHDRAWALS("GetWithdrawals"),

    @JsonProperty("Refund")
    REFUND("Refund"),

    @JsonProperty("credit")
    CREDIT("credit"),

    @JsonProperty("debit")
    DEBIT("debit"),

    @JsonProperty("pending")
    PENDING("pending"),

    @JsonProperty("cancel")
    CANCEL("cancel"),

    @JsonProperty("account")
    ACCOUNT("account"),

    @JsonProperty("payoutconfirmation")
    PAYOUT_CONFIRMATION("payoutconfirmation"),

    @JsonProperty("Charge")
    CHARGE("Charge"),

    @JsonProperty("SelectAccount")
    SELECT_ACCOUNT("SelectAccount"),

    @JsonProperty("AccountPayout")
    ACCOUNT_PAYOUT("AccountPayout");

    private final String jsonName;

    Method(final String s) {
        jsonName = s;
    }

    public boolean equalsName(final String otherName) {
        return otherName != null && jsonName.equals(otherName);
    }

    public String toString() {
        return jsonName;
    }
}
