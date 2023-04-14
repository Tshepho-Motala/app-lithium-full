package lithium.service.cashier.processor.inpay.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lithium.service.cashier.client.objects.GeneralError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.MessageSource;

@ToString
@AllArgsConstructor()
public enum InPayPaymentErrors {
    AMOUNT_TOO_LOW("amount_too_low", GeneralError.GENERAL_ERROR, "Amount too low"),
    AWAITING_KYC_DOCUMENTS("awaiting_kyc_documents", GeneralError.CONTACT_YOUR_BANK, "Awaiting KYC Documents"),
    AWAITING_EXTENDED_KYC("awaiting_extended_kyc", GeneralError.CONTACT_YOUR_BANK, "Extended KYC documents required"),
    ACCOUNT_NOT_SUITABLE_EFT("the_account_is_not_suitable_for_EFT_transaction", GeneralError.CONTACT_YOUR_BANK,"Cancelled/Returned - The account is not suitable for EFT transaction"),
    ACCOUNT_NOT_SUITABLE_IMPS("the_account_is_not_suitable_for_IMPS_transaction", GeneralError.CONTACT_YOUR_BANK,"Cancelled - The account is not suitable for IMPS transaction"),
    BANK_ACCOUNT_BLOCKED("beneficiary_bank_account_blocked", GeneralError.CONTACT_YOUR_BANK, "Returned - Beneficiary bank account is blocked"),
    BENEFICIARY_RETURNED_REQUEST("beneficiary_request_returned_payment_as_per_beneficiary_request", GeneralError.CONTACT_YOUR_BANK, "Returned - Returned payment as per beneficiary request"),
    BENEFICIARY_BANK_ACCOUNT_CLOSED("beneficiary_bank_account_closed", GeneralError.CONTACT_YOUR_BANK, "Beneficiary bank account is closed"),
    BENEFICIARY_BANK_ACCOUNT_NAME_MISMATCH("beneficiary_bank_account_name_mismatch", GeneralError.VERIFY_INPUT_DETAILS, "Rejected by bank"),
    BENEFICIARY_BANK_IS_UNABLE_TO_APPLY_FUNDS("beneficiary_bank_is_unable_to_apply_funds", GeneralError.GENERAL_ERROR, "Rejected by bank"),
    BIC_IS_NOT_SEPA("the_BIC_is_not_SEPA_reachable", GeneralError.VERIFY_INPUT_DETAILS,"Cancelled/Returned - The BIC is not SEPA reachable"),
    CANCELLED_BY_INPAY("order_cancelled_by_inpay", GeneralError.CONTACT_YOUR_BANK, "Order cancelled by Inpay Operator in Inpay Admin"),
    CANCELLED_BY_MERCHANT("order_cancelled_by_merchant", GeneralError.CONTACT_YOUR_BANK,"Order cancelled by ###merchant_name###"),
    CANCELLED_BY_USER("order_cancelled_by_user", GeneralError.CANCEL_TRANSACTION, "Order cancelled by user"),
    CHARACTER_NOT_SUPPORTED("invalid_data_kanji_character_is_not_supported", GeneralError.VERIFY_INPUT_DETAILS, "Cancelled - Kanji character is not supported"),
    COMPLIANCE_CHECK("compliance_check", GeneralError.CONTACT_YOUR_BANK, "Cancelled - Rejected due to compliance"),
    EXPIRED("expired", GeneralError.CONTACT_YOUR_BANK, "Order Expired"),
    FREETEXT("freetext", GeneralError.CONTACT_YOUR_BANK, "Refer to invoice status page"),
    INVALID_DATA_COUNTRY_MISMATCH("invalid_data_country_mismatch", GeneralError.VERIFY_INPUT_DETAILS, "Country mismatch"),
    INVALID_DATA_INCORRECT_SWIFT_BIC("invalid_data_incorrect_swift_bic", GeneralError.VERIFY_INPUT_DETAILS, "Incorrect or missing SWIFT/BIC"),
    INVALID_DATA_CURRENCY_MISMATCH("invalid_data_currency_mismatch", GeneralError.VERIFY_INPUT_DETAILS, "Currency mismatch"),
    INVALID_DATA_INCORRECT_BENEFICIARY_BANK_DETAILS("invalid_data_incorrect_beneficiary_bank_details", GeneralError.VERIFY_INPUT_DETAILS, " Incorrect beneficiary bank details"),
    INVALID_DATA_MISMATCH_ACCOUNT_AND_BIC("invalid_data_mismatch_account_and_bic", GeneralError.VERIFY_INPUT_DETAILS, "Mismatch account and BIC"),
    INVALID_DATA_MISMATCH_COUNTRY_AND_BIC("invalid_data_mismatch_country_and_bic", GeneralError.VERIFY_INPUT_DETAILS, "Mismatch country and BIC"),
    INVALID_CREDITOR_ACCOUNT("invalid_creditor_account", GeneralError.ANOTHER_METHOD, "Invalid creditor account"),
    INVALID_CREDITOR("invalid_creditor", GeneralError.ANOTHER_METHOD, "Invalid creditor"),
    INVALID_ULTIMATE_DEBTOR("invalid_ultimate_debtor", GeneralError.VERIFY_INPUT_DETAILS, "Invalid ultimate debtor"),
    INVALID_GENERAL_DATA("invalid_general_data", GeneralError.VERIFY_INPUT_DETAILS, "Invalid general payment request data"),
    INVALID_DEBTOR("invalid_debtor", GeneralError.VERIFY_INPUT_DETAILS, "Invalid debtor"),
    INTERNAL_POLICY_BANK_POLICY("internal_policy_bank_policy", GeneralError.CONTACT_YOUR_BANK, "Bank policy"),
    INVALID_DATA_INCORRECT_ACCOUNT_NUMBER("invalid_data_incorrect_account_number", GeneralError.VERIFY_INPUT_DETAILS, "Incorrect or missing bank account number"),
    INVALID_DATA_INCORRECT_BRANCH_CODE("invalid_data_incorrect_branch_code", GeneralError.ANOTHER_METHOD, "Incorrect branch code"),
    INVALID_DATA_INCORRECT_BENEFICIARY_DETAILS("invalid_data_incorrect_beneficiary_details", GeneralError.VERIFY_INPUT_DETAILS, "Incorrect or missing beneficiary details"),
    INVALID_SENDER_DETAILS("invalid_data_incorrect_or_missing_sender_details", GeneralError.VERIFY_INPUT_DETAILS, "Cancelled/Returned - Incorrect or missing sender details"),
    INVALID_BENEFICIARY_NAME("invalid_data_incorrect_beneficiary_name", GeneralError.VERIFY_INPUT_DETAILS, "Cancelled - Incorrect or missing beneficiary name"),
    KYC_DOCUMENTATION_NOT_PROVIDED_WITHIN_REASONABLE_TIME("kyc_documentation_not_provided_within_reasonable_time", GeneralError.CONTACT_YOUR_BANK, "KYC documentation not provided within reasonable time"),
    MISSING_CAPABILITY("missing_capability", GeneralError.GENERAL_ERROR, "Cancelled - Missing capability"),
    NOT_SUPPORTED_BY_YOUR_CONTRACT("not_supported_by_your_contract", GeneralError.CONTACT_YOUR_BANK, "Not supported by your contract"),
    NOT_SUITABLE_PAYMENT("not_suitable_for_faster_payment_service"	, GeneralError.CONTACT_YOUR_BANK, "Cancelled - Not suitable for Faster Payment Service"),
    PAYMENT_NOT_COLLECTED("payment_not_collected", GeneralError.CONTACT_YOUR_BANK,"Returned - payment not collected"),
    REQUESTED_BY_CUSTOMER("requested_by_customer", GeneralError.CANCEL_TRANSACTION, "Requested by customer"),
    REQUESTED_BY_MERCHANT("requested_by_merchant", GeneralError.GENERAL_ERROR, "Returned - Requested by merchant"),
    REJECTED_BY_BANK_BENEFICIARY_FLAGGED("rejected_by_bank_beneficiary_flagged", GeneralError.CONTACT_YOUR_BANK, "Rejected by bank"),
    REJECTED_DUE_TO_INTERNAL_POLICY("rejected_due_to_internal_policy", GeneralError.CONTACT_YOUR_BANK, "Rejected due to internal policy"),
    REASON_NOT_SPECIFIED("reason_not_specified_by_beneficiary_bank", GeneralError.CONTACT_YOUR_BANK,"Cancelled/Returned - Reason not specified by beneficiary bank"),
    SUSPICIOUS_ACCOUNT_NOT_RECOGNIZED("suspicious_account_not_recognized", GeneralError.CONTACT_YOUR_BANK, "Rejected by bank"),
    SERVICE_UNAVAILABLE("service_unavailable", GeneralError.TRY_AGAIN_LATER, "Service unavailable"),
    SUSPICIOUS_NAME_MISMATCH("suspicious_name_mismatch", GeneralError.VERIFY_INPUT_DETAILS, "Name mismatch"),
    TECHNICAL_ISSUE_UNKNOWN("technical_issue_unknown", GeneralError.GENERAL_ERROR, "Technical issue"),
    TEST_PAYMENT("test_payment", GeneralError.VERIFY_INPUT_DETAILS, "Cancelled/Returned - Test Payment"),
    UNABLE_TO_APPROVE_KYC_DOCUMENTATION("unable_to_approve_kyc_documentation", GeneralError.CONTACT_YOUR_BANK, "Unable to approve KYC documentation"),
    UNABLE_TO_APPLY_FUNDS("processing_bank_is_unable_to_apply_funds", GeneralError.CONTACT_YOUR_BANK, "Cancelled - Processing bank is unable to apply funds"),
    VALIDATION_SCHEMA_FAILED("validation_schema_failed", GeneralError.GENERAL_ERROR, "Cancelled - Validation schema failed"),
    WAITING_FUTURE_EXECUTION("waiting_future_execution", GeneralError.CONTACT_YOUR_BANK, "Payment will be executed on YYYY-MM-DD");
    @Getter
    @Accessors(fluent = true)
    private final String code;
    @Getter
    @Accessors(fluent = true)
    private final GeneralError generalError;
    @Getter
    @Accessors(fluent = true)
    private final String description;

    @JsonValue
    public String getCode() {
        return code;
    }

    public GeneralError getGeneralError() {
        return this.generalError;
    }

    public String getResponseMessageLocal(MessageSource messageSource, String domainName, String lang) {
        return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
    }

	public String getDescription() {
		return description;
	}

	@JsonCreator
    public static InPayPaymentErrors fromErrorCode(String errorCode) {
        for (InPayPaymentErrors s : InPayPaymentErrors.values()) {
            if (s.getCode().equals(errorCode)) {
                return s;
            }
        }
        return TECHNICAL_ISSUE_UNKNOWN;
    }
}

