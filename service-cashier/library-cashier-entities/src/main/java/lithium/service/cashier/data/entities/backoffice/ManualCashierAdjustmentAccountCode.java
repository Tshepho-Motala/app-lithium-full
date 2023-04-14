package lithium.service.cashier.data.entities.backoffice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ManualCashierAdjustmentAccountCode {
    GF_MISSING_DEPOSIT("GF_MISSING_DEPOSIT", true, false, TransactionType.DEPOSIT, true),
    GF_MISSING_WITHDRAWAL("GF_MISSING_WITHDRAWAL", false, true, TransactionType.WITHDRAWAL, true),
    GF_PAYMENT_ERROR("GF_PAYMENT_ERROR", false, true, TransactionType.DEPOSIT, false),
    GF_WITHDRAWAL_ERROR("GF_WITHDRAWAL_ERROR", true, false, TransactionType.WITHDRAWAL, false);
    
    @Getter
    private String code;
    @Getter
    private boolean debit;
    @Getter
    private boolean credit;
    @Getter(onMethod_=@JsonIgnore)
    private TransactionType transactionType;
    @Getter(onMethod_=@JsonIgnore)
    private boolean isSuccessTransaction;


    public static List<ManualCashierAdjustmentAccountCode> getAllowedCodes(Transaction transaction) {
        return Arrays.stream(ManualCashierAdjustmentAccountCode.values())
                .filter(code -> ManualCashierAdjustmentAccountCode.isValidForTransaction(code, transaction))
                .collect(Collectors.toList());
    }

    public static ManualCashierAdjustmentAccountCode fromCode(String code) {
        for (ManualCashierAdjustmentAccountCode accountCode : ManualCashierAdjustmentAccountCode.values()) {
            if (code.equals(accountCode.getCode())) {
                return accountCode;
            }
        }
        throw new IllegalArgumentException("Account code not found");
    }

    public static boolean isValidForTransaction(ManualCashierAdjustmentAccountCode code, Transaction transaction) {
        return code.isSuccessTransaction == transaction.getStatus().isSuccess() && code.transactionType == transaction.getTransactionType();
    }
    
}
