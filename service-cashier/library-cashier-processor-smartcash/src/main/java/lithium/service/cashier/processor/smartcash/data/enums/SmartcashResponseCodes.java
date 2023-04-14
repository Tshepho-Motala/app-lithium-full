package lithium.service.cashier.processor.smartcash.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lithium.service.cashier.client.objects.GeneralError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

@Slf4j
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum SmartcashResponseCodes {
    unknown_error("", GeneralError.GENERAL_ERROR, "Unknown Smartcash response code"),
    COLLECTION_DEFAULT("DP01100001000", GeneralError.TRY_AGAIN_LATER,	"Unknown or unclassified error. User would be notified to try again."),
    COLLECTION_SUCCESS("DP01100001001", GeneralError.TRY_AGAIN_LATER,	"Transaction is successful."),
    COLLECTION_INCORRECT_PIN("DP01100001002", GeneralError.GENERAL_ERROR,		"User enters incorrect pin."),
    COLLECTION_LIMIT_EXCEEDED("DP01100001003", GeneralError.GENERAL_ERROR,"User Exceeds withdrawal transaction limit."),
    COLLECTION_INVALID_AMOUNT( "DP01100001004", GeneralError.GENERAL_ERROR, "User enters invalid amount."),
    COLLECTION_FAILED("DP01100001005"	, GeneralError.GENERAL_ERROR, "User has dropped in between the transaction."),
    COLLECTION_IN_PROCESS("DP01100001006", GeneralError.TRY_AGAIN_LATER,		"User is in between the Process. Transaction in process."),
    COLLECTION_INSUFFICIENT_FUNDS("DP01100001007", GeneralError.GENERAL_ERROR, "User has insufficient funds to complete the transaction."),
    COLLECTION_USER_NOT_ALLOWED("DP01100001008", GeneralError.GENERAL_ERROR,		"User not allowed"),
    COLLECTION_INVALID_INITIATE("DP01100001009", GeneralError.GENERAL_ERROR,		"Initiate is invalid"),
    COLLECTION_NOT_PERMITTED("DP01100001010", GeneralError.GENERAL_ERROR,		"User not allowed as payer"),
    COLLECTION_DO_NOT_HONOUR("DP01100001011", GeneralError.GENERAL_ERROR,		"Transaction is already completed"),
    COLLECTION_INVALID_MOBILE_NUMBER("DP01100001012", GeneralError.GENERAL_ERROR,		"Invalid mobile number"),
    COLLECTION_REFUSED("DP01100001013", GeneralError.GENERAL_ERROR,		"The transaction was refused"),
    COLLECTION_TRANSACTION_NOT_ALLOWED("DP01100001014", GeneralError.GENERAL_ERROR,		"Transaction is not allowed"),
    COLLECTION_BAD_REQUEST("DP01100001015", GeneralError.GENERAL_ERROR,		"Bad request"),
    COLLECTION_TRANSACTION_ALREADY_EXIST("DP01100001016", GeneralError.GENERAL_ERROR,		"This transaction already exists"),


    DISBURSEMENT_DEFAULT("DP01000001000", GeneralError.TRY_AGAIN_LATER,	"Unknown or unclassified error. User would be notified to try again."),
    DISBURSEMENT_SUCCESS("DP01000001001", GeneralError.TRY_AGAIN_LATER,	"Transaction is successful."),
    DISBURSEMENT_INCORRECT_PIN("DP01000001002", GeneralError.GENERAL_ERROR,		"User enters incorrect pin."),
    DISBURSEMENT_LIMIT_EXCEEDED("DP01000001003", GeneralError.LIMIT_EXCEEDED,"User Exceeds withdrawal transaction limit."),
    DISBURSEMENT_INVALID_AMOUNT( "DP01000001004", GeneralError.GENERAL_ERROR, "User enters invalid amount."),
    DISBURSEMENT_FAILED("DP01000001005"	, GeneralError.ANOTHER_METHOD, "User has dropped in between the transaction."),
    DISBURSEMENT_IN_PROCESS("DP01000001006", GeneralError.TRY_AGAIN_LATER,		"User is in between the Process. Transaction in process."),
    DISBURSEMENT_INSUFFICIENT_FUNDS("DP01000001007", GeneralError.INSUFFICIENT_FUNDS, "User has insufficient funds to complete the transaction."),
    DISBURSEMENT_USER_NOT_ALLOWED("DP01000001008", GeneralError.GENERAL_ERROR,		"User not allowed"),
    DISBURSEMENT_INVALID_INITIATE("DP01000001009", GeneralError.GENERAL_ERROR,		"Initiate is invalid"),
    DISBURSEMENT_NOT_PERMITTED("DP01000001010", GeneralError.GENERAL_ERROR,		"User not allowed as payer"),
    DISBURSEMENT_DO_NOT_HONOUR("DP01000001011", GeneralError.GENERAL_ERROR,		"Transaction is already completed"),
    DISBURSEMENT_INVALID_MOBILE_NUMBER("DP01000001012", GeneralError.GENERAL_ERROR,		"Invalid mobile number"),
    DISBURSEMENT_REFUSED("DP01000001013", GeneralError.GENERAL_ERROR,		"The transaction was refused"),
    DISBURSEMENT_TRANSACTION_NOT_ALLOWED("DP01000001014", GeneralError.GENERAL_ERROR,		"Transaction is not allowed"),
    DISBURSEMENT_BAD_REQUEST("DP01000001015", GeneralError.GENERAL_ERROR,		"Bad request"),
    DISBURSEMENT_TRANSACTION_ALREADY_EXIST("DP01000001016", GeneralError.GENERAL_ERROR,		"This transaction already exists");

    @Getter
    @Accessors(fluent = true)
    private String code;
    @Getter
    @Accessors(fluent = true)
    private GeneralError generalError;
    @Getter
    @Accessors(fluent = true)
    private String description;

    @JsonValue
    public String getCode() {
        return code;
    }

    public boolean isError() { return this.generalError != null; }

    public GeneralError getGeneralError() { return this.generalError; }

    public String getGeneralErrorLocal(MessageSource messageSource, String domainName, String lang) {
        return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
    }


    @JsonCreator
    public static SmartcashResponseCodes fromResponseCode(String errorCode) {
        if (errorCode != null && !errorCode.isEmpty()) {
            for (SmartcashResponseCodes s : SmartcashResponseCodes.values()) {
                if (errorCode.equalsIgnoreCase(s.getCode())) {
                    return s;
                }
            }
        }
        return unknown_error;
    }
}
