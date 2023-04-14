package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccountCode {
    
    GF_CHARGEBACK_RECOVERY("GF_CHARGEBACK_RECOVERY", true, false),
    DORMANT_ACCOUNT("DORMANT_ACCOUNT", true, false),
    DORMANT_ACCOUNT_FEE("DORMANT_ACCOUNT_FEE", true, false),
    CASINO_MANUAL_CASINO_WINNINGS_DEBIT("CASINO_MANUAL_CASINO_WINNINGS_DEBIT", false, true),
    GF_DEPOSIT_BONUS("GF_DEPOSIT_BONUS", false, true),
    GF_DORMANT_ACCOUNT_CORRECTION("GF_DORMANT_ACCOUNT_CORRECTION", false, true),
    GF_FRAUD_CB_ACCOUNT ("GF_FRAUD_CB_ACCOUNT", true, false),
    GF_MANUAL_BALANCE_ADJUST_WITHDRAWAL("GF_MANUAL_BALANCE_ADJUST (WITHDRAWAL)", true, false),
    GF_NEGATIVE_BALANCE_ADJUSTMENT("GF_NEGATIVE_BALANCE_ADJUSTMENT", false, true),
    GF_NEGATIVE_BALANCE_ADJUSTMENT_MANUAL("GF_NEGATIVE_BALANCE_ADJUSTMENT_MANUAL", true, false),
    CASINO_MANUAL_CASINO_WINNINGS_CREDIT("CASINO_MANUAL_CASINO_WINNINGS_CREDIT", true, false),
    GF_DEPOSIT_BONUS_CANCELLATION("GF_DEPOSIT_BONUS_CANCELLATION", true, false),
    GF_MANUAL_BALANCE_ADJUST("GF_MANUAL_BALANCE_ADJUST", false, true),
    GF_MANUAL_BANK_TRANSFER("GF_MANUAL_BANK_TRANSFER", true, false),
    GF_MANUAL_BANK_TRANSFER_CORRECTION("GF_MANUAL_BANK_TRANSFER_CORRECTION", false, true),
    GF_MANUAL_PSP("GF_MANUAL_PSP", true, false),
    GF_MANUAL_PSP_CORRECTION("GF_MANUAL_PSP_CORRECTION", false, true),
    MANUAL_BONUS_FREE_FUNDS("MANUAL_BONUS_FREE_FUNDS", true, true),
    MANUAL_BONUS_POKER("MANUAL_BONUS_POKER", true, true),
    MANUAL_BONUS_CASINO("MANUAL_BONUS_CASINO", true, true),
    MANUAL_WITHDRAWAL_FEE("MANUAL_WITHDRAWAL_FEE", true, true),
    AFFILIATE_PAYMENT("AFFILIATE_PAYMENT", true, true),
    MANUAL_BONUS_VIRTUAL("MANUAL_BONUS_VIRTUAL", false, false),
    PLAYER_BALANCE_WRITEOFF("PLAYER_BALANCE_WRITEOFF", true, true);
    
    @Getter
    private String accountTypeCode;
    @Getter
    private boolean debit;
    @Getter
    private boolean credit;
    
    public static boolean isValid(String accountCode, long amountCents) {
        AccountCode code = AccountCode.fromCode(accountCode);
        if (code.isCredit() && amountCents > 0 || code.isDebit() && amountCents < 0) {
            return true;
        }
        return false;
    }

    public static AccountCode fromCode(String code) {
        for (AccountCode account : AccountCode.values()) {
            if (code.equals(account.getAccountTypeCode())) {
                return account;
            }
        }
        throw new IllegalArgumentException("Account code not found");
    }
    
}
