package lithium.service.cashier.processor.paynl.data.enums;

import lithium.service.cashier.client.objects.GeneralError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.MessageSource;

@ToString
@AllArgsConstructor
public enum ErrorCodes {

    TRANSACTION_NOT_FOUND("PAY-001", GeneralError.VERIFY_INPUT_DETAILS,"Transaction not found"),
    SERVICE_NOT_FOUND("PAY-004", GeneralError.VERIFY_INPUT_DETAILS, "Service not found"),
    CHARGEBACK("PAY-013", GeneralError.GENERAL_ERROR, "Transaction in chargeback mode"),
    REFUND_TO_FAST("PAY-014", GeneralError.GENERAL_ERROR, "Refund too Fast"),
    REFUND_AMOUNT_INVALID("PAY-015", GeneralError.GENERAL_ERROR,"Refund amount invalid"),
    CARD_NOT_FOUND("PAY-404", GeneralError.VERIFY_CARD_DETAILS,"Card not found"),
    PAYMENT_PROFILEID_OR_AMOUNT_IS_INVALID("PAY‑405", GeneralError.VERIFY_INPUT_DETAILS,"Parameter 'paymentProfileId or amount' is invalid: One of them should be set"),
    TRANSACTION_CANT_BE_CANCELLED("PAY‑406", GeneralError.GENERAL_ERROR,"Transaction can't be cancelled, It's in a PAID, VERIFY or AUTHORISE state."),
    BOARDING_OF_PAYMENT_OPTION_IS_NOT_COMPLETED("PAY‑407", GeneralError.VERIFY_INPUT_DETAILS,"Boarding of Payment option (PAYMENT_OPTION_ID) is not completed for this sales location (SL-####-####)"),
    BOARDING_OF_PAYMENT_OPTION_IS_NOT_ACTIVATED("PAY‑408", GeneralError.VERIFY_INPUT_DETAILS,"Payment option (PAYMENT_OPTION_ID) is not activated for this Sales Location (SL-####-####) or merchant (M-####-####)"),
    AMOUNT_IS_NOT_ALLOWED("PAY-409", GeneralError.VERIFY_INPUT_DETAILS,"Amount is not allowed (AMOUNT)"),
    MAXIMUM_AMOUNT_EXCEEDED("PAY-410", GeneralError.LIMIT_BALANCE_CAP,"Maximum amount (AMOUNT) exceeded for payment option (PAYMENT_OPTION_ID)"),
    NO_ACTIVE_AUTHORISATION ("PAY-411", GeneralError.VERIFY_INPUT_DETAILS,"There is no active Authorisation for this transaction"),
    TRANSACTION_IS_ALREADY_CAPTURED("PAY-412", GeneralError.GENERAL_ERROR,"This Transaction is already captured"),
    MANDATORY_FIELD_IS_MISSING("PAY-2828", GeneralError.VERIFY_INPUT_DETAILS,"Mandatory field is missing, invalid or has an empty value."),
    INVALID_CURRENCY("PAY-2836", GeneralError.VERIFY_INPUT_DETAILS,"ivalid_currency"),
    INVALID_TRANSACTION_TYPE("PAY-2869", GeneralError.VERIFY_INPUT_DETAILS,"Invalid transaction type. Should be one of: MIT, CIT"),
    FAILED_TO_ADD_REFUND("PAY-3000", GeneralError.GENERAL_ERROR,"Failed to add refund: Refunden naar clearing rekening niet geactiveerd.(refunding to clearing account is not activated)."),
    INSUFFICIENT_BALANCE("PAY-3002", GeneralError.INSUFFICIENT_FUNDS,"Transaction not successful because of an insufficient balance"),
    REFUND_WAS_NOT_COMPLETE("PAY-3003", GeneralError.GENERAL_ERROR,"Refund was not complete"),
    HOLDER_NAME_IS_NOT_PROVIDED("PAY-3008", GeneralError.VERIFY_INPUT_DETAILS,"Bank account holder name is not provided, log in to the PAY admin and enter the name of the bank account holder."),
    UNKNOWN_ERROR("unknown", GeneralError.GENERAL_ERROR, "Unknown error");
    
    @Getter
    private String code;
    @Getter
    private GeneralError generalError;
    @Getter
    private String message;

    public String getResponseMessageLocal(MessageSource messageSource, String domainName, String lang) {
        return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
    }
    
    public static ErrorCodes fromErrorCode(String errorCode) {
        for (ErrorCodes e : ErrorCodes.values()){
            if(e.code.equals(errorCode)){
                return e;
            }
        }
        return UNKNOWN_ERROR;
    }
}
