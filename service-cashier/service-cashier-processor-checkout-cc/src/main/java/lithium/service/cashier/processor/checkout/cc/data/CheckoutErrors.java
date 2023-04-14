package lithium.service.cashier.processor.checkout.cc.data;

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
public enum CheckoutErrors {
    unknown_error("422000", GeneralError.GENERAL_ERROR, "Validation error."),
    address_invalid("422001", GeneralError.GENERAL_ERROR,"The shipping address is invalid."),
    amount_exceeds_balance("422002", GeneralError.GENERAL_ERROR, "The payment amount exceeds the balance."),
    amount_invalid("422003", GeneralError.GENERAL_ERROR, "The payment amount is invalid."),
    api_calls_quota_exceeded("422004", GeneralError.GENERAL_ERROR, "The quota for API calls has been exceeded."),
    billing_descriptor_city_invalid("422005", GeneralError.GENERAL_ERROR, "The city from which the charge originated is invalid."),
    billing_descriptor_city_required("422006", GeneralError.GENERAL_ERROR, "The city from which the charge originated is required."),
    billing_descriptor_name_invalid("422008", GeneralError.GENERAL_ERROR, "The dynamic description of the charge is invalid."),
    billing_descriptor_name_required("422009", GeneralError.GENERAL_ERROR, "A dynamic description of the charge is required."),
    business_invalid("422010", GeneralError.GENERAL_ERROR, "The business settings are invalid."),
    business_settings_missing("422011", GeneralError.GENERAL_ERROR, "The business settings are missing."),
    capture_value_greater_than_authorized("422012", GeneralError.GENERAL_ERROR,"The capture value is greater than the authorized value."),
    capture_value_greater_than_remaining_authorized("422013", GeneralError.GENERAL_ERROR, "The capture value is greater than the remaining authorized value."),
    card_authorization_failed("422014", GeneralError.GENERAL_ERROR, "The card authorization has failed."),
    card_disabled("422015", GeneralError.GENERAL_ERROR, "The card is disabled."),
    card_expired("422016", GeneralError.GENERAL_ERROR, "The card is expired."),
    card_expiry_month_invalid("422017", GeneralError.GENERAL_ERROR, "The two-digit expiry month is invalid."),
    card_expiry_month_required("422018", GeneralError.GENERAL_ERROR, "A two-digit expiry month is required."),
    card_expiry_year_invalid("422019", GeneralError.GENERAL_ERROR, "The four-digit expiry year is invalid."),
    card_expiry_year_required("422020", GeneralError.GENERAL_ERROR, "A four-digit expiry year is required."),
    card_holder_invalid("422021", GeneralError.GENERAL_ERROR, "The cardholder is invalid."),
    card_not_found("422022", GeneralError.GENERAL_ERROR, "The card is not found."),
    card_number_invalid("422023", GeneralError.GENERAL_ERROR, "The card number is invalid."),
    card_number_required("422024", GeneralError.GENERAL_ERROR, "The card number is required."),
    channel_details_invalid("422025", GeneralError.GENERAL_ERROR, "The channel details are invalid."),
    channel_url_missing("422026", GeneralError.GENERAL_ERROR, "The channel URL is missing."),
    charge_details_invalid("422027", GeneralError.GENERAL_ERROR, "The charge details are invalid."),
    city_invalid("422028", GeneralError.GENERAL_ERROR, "The city from which the charge originated is invalid."),
    country_address_invalid("422029", GeneralError.GENERAL_ERROR, "The first or second line of the payment source owner's billing address is invalid."),
    country_invalid("422030", GeneralError.GENERAL_ERROR, "The two-letter ISO country code of the payment source owner's billing address is invalid."),
    country_phone_code_invalid("422031", GeneralError.GENERAL_ERROR, "The international country calling code is invalid."),
    country_phone_code_length_invalid("422032", GeneralError.GENERAL_ERROR, "The international country calling code length is invalid."),
    currency_invalid("422033", GeneralError.GENERAL_ERROR, " The three-letter ISO currency code is invalid."),
    currency_required("422034", GeneralError.GENERAL_ERROR, "The three-letter ISO currency code is required."),
    customer_already_exists("422035", GeneralError.GENERAL_ERROR, "The customer details already exist."),
    customer_email_invalid("422037", GeneralError.GENERAL_ERROR, "The email address associated with the customer is invalid."),
    customer_id_invalid("422038", GeneralError.GENERAL_ERROR, "The customer identifier is invalid."),
    customer_mismatch("422039", GeneralError.GENERAL_ERROR, "The customer ID you provided does not match the ID associated with the payment source."),
    customer_not_found("422040", GeneralError.GENERAL_ERROR, "The customer's details cannot be found."),
    customer_number_invalid("422041", GeneralError.GENERAL_ERROR, "The customer number is invalid."),
    customer_plan_edit_failed("422042", GeneralError.GENERAL_ERROR, "Editing the customer plan has failed."),
    customer_plan_id_invalid("422043", GeneralError.GENERAL_ERROR, "The customer plan identifier is invalid."),
    cvv_invalid("422044", GeneralError.GENERAL_ERROR, "The CVV is invalid."),
    destination_amount_invalid("422045", GeneralError.GENERAL_ERROR, "The payout destination amount is invalid."),
    destination_id_invalid("422046", GeneralError.GENERAL_ERROR, "The payout destination identifier is invalid."),
    destination_token_required("422047", GeneralError.GENERAL_ERROR, "The payout destination token is required."),
    destination_token_invalid("422048", GeneralError.GENERAL_ERROR, "The payout destination token is invalid."),
    email_in_use("422049", GeneralError.GENERAL_ERROR, "The email address is already in use."),
    email_invalid("422050", GeneralError.GENERAL_ERROR, "The email address is invalid."),
    email_required("422051", GeneralError.GENERAL_ERROR, "The email address is required."),
    endpoint_invalid("422052", GeneralError.GENERAL_ERROR, "The endpoint is invalid."),
    expiry_date_format_invalid("422053", GeneralError.GENERAL_ERROR, "The expiry date format is invalid."),
    fail_url_invalid("422054", GeneralError.GENERAL_ERROR, "The failure URL is invalid."),
    first_name_required("422055", GeneralError.GENERAL_ERROR, "The account holder's first name is required."),
    last_name_required("422056", GeneralError.GENERAL_ERROR, "The account holder's last name is required."),
    ip_address_invalid("422057", GeneralError.GENERAL_ERROR, "The IP address used to make the payment is invalid."),
    issuer_network_unavailable("422058", GeneralError.GENERAL_ERROR, "The issuer network is unavailable."),
    metadata_key_invalid("422059", GeneralError.GENERAL_ERROR, "The metadata key is invalid."),
    parameter_invalid("422060", GeneralError.GENERAL_ERROR, "The parameter is invalid."),
    password_invalid("422061", GeneralError.GENERAL_ERROR, "The password is invalid."),
    payment_expired("422062", GeneralError.GENERAL_ERROR, "The payment has expired."),
    payment_invalid("422063", GeneralError.GENERAL_ERROR, "The payment is invalid."),
    payment_method_invalid("422064", GeneralError.GENERAL_ERROR, "The payment method is invalid."),
    payment_source_required("422065", GeneralError.GENERAL_ERROR, "The payment source linked to a specific customer is required."),
    payment_type_invalid("422066", GeneralError.GENERAL_ERROR, "The payment type is invalid."),
    phone_number_invalid("422067", GeneralError.GENERAL_ERROR, "The phone number associated with the shipping address is invalid."),
    phone_number_length_invalid("422068", GeneralError.GENERAL_ERROR, "The length of the phone number associated with the shipping address is invalid."),
    previous_payment_id_invalid("422069", GeneralError.GENERAL_ERROR, "The previous payment identifier is invalid."),
    recipient_account_number_invalid("422070", GeneralError.GENERAL_ERROR, "The recipient's account number is invalid."),
    recipient_account_number_required("422071", GeneralError.GENERAL_ERROR, "	The recipient's account number is required."),
    recipient_dob_invalid("422072", GeneralError.GENERAL_ERROR, "The recipient's date of birth (YYYY-MM-DD) is invalid."),
    recipient_dob_required("422073", GeneralError.GENERAL_ERROR, "The recipient's date of birth (YYYY-MM-DD) is required."),
    recipient_last_name_required("422074", GeneralError.GENERAL_ERROR, "The recipient's last name is required."),
    recipient_zip_invalid("422075", GeneralError.GENERAL_ERROR, "The first part of the recipient's UK postcode is required."),
    recipient_zip_required("422076", GeneralError.GENERAL_ERROR, "The first part of the recipient's UK postcode is required."),
    recurring_plan_exists("422077", GeneralError.GENERAL_ERROR, "The recurring plan exists."),
    recurring_plan_not_exist("422078", GeneralError.GENERAL_ERROR, "The recurring plan does not exist."),
    recurring_plan_removal_failed("422079", GeneralError.GENERAL_ERROR, "Removing the recurring plan has failed."),
    request_invalid("422080", GeneralError.GENERAL_ERROR, "The request is invalid."),
    request_json_invalid("422081", GeneralError.GENERAL_ERROR, "The JSON in the request is invalid."),
    risk_enabled_required("422082", GeneralError.GENERAL_ERROR, "The risk check that is enabled is required."),
    server_api_not_allowed("422083", GeneralError.GENERAL_ERROR, "The full API is not enabled on your sandbox account."),
    source_email_invalid("422084", GeneralError.GENERAL_ERROR, "The payment source owner's email address is invalid."),
    source_email_required("422085", GeneralError.GENERAL_ERROR, "The payment source owner's email address is required."),
    source_id_invalid("422086", GeneralError.GENERAL_ERROR, "The payment source identifier is invalid."),
    source_id_or_email_required("422087", GeneralError.GENERAL_ERROR, "The payment source identifier or email address is required."),
    source_id_required("422088", GeneralError.GENERAL_ERROR, "The payment source identifier is required."),
    source_id_unknown("422089", GeneralError.GENERAL_ERROR, "The payment source identifier is unknown."),
    source_invalid("422090", GeneralError.GENERAL_ERROR, "The payment source is invalid."),
    source_or_destination_required("422091", GeneralError.GENERAL_ERROR, "The payment source or destination is required."),
    source_token_invalid("422092", GeneralError.GENERAL_ERROR, "The Checkout.com token is invalid."),
    source_token_required("422093", GeneralError.GENERAL_ERROR, "The Checkout.com token number is required."),
    source_token_type_required("422094", GeneralError.GENERAL_ERROR, "The source token type is required."),
    source_token_type_invalid("422095", GeneralError.GENERAL_ERROR, "The source token type is invalid."),
    source_type_required("422096", GeneralError.GENERAL_ERROR, "The payment source type is required."),
    success_url_invalid("422097", GeneralError.GENERAL_ERROR, "The success URL provided is invalid."),
    threeds_malfunction("422098", GeneralError.GENERAL_ERROR, "3DS has malfunctioned."),
    threeds_not_configured("422099", GeneralError.GENERAL_ERROR, "3DS is not configured."),
    threeds_not_enabled_for_card("422100", GeneralError.GENERAL_ERROR, "3DS is not enabled for card."),
    threeds_not_supported("422101", GeneralError.GENERAL_ERROR, "3DS is not supported."),
    threeds_payment_required("422102", GeneralError.GENERAL_ERROR, "3DS payment required."),
    token_expired("422103", GeneralError.GENERAL_ERROR, "The Checkout.com token has expired."),
    token_in_use("422104", GeneralError.GENERAL_ERROR, "The Checkout.com token is in use."),
    token_invalid("422105", GeneralError.GENERAL_ERROR, "The Checkout.com token is invalid."),
    token_required("422106", GeneralError.GENERAL_ERROR, "The Checkout.com token is required."),
    token_type_required("422107", GeneralError.GENERAL_ERROR, "The Checkout.com token type is required."),
    token_used("422108", GeneralError.GENERAL_ERROR, "The Checkout.com token has already been used."),
    void_amount_invalid("422109", GeneralError.GENERAL_ERROR, "The void request amount is invalid."),
    wallet_id_invalid("422110", GeneralError.GENERAL_ERROR, "The wallet identifier is invalid."),
    zip_invalid("422111", GeneralError.GENERAL_ERROR, "The first part of the UK postcode is invalid."),
    processing_key_required("422112", GeneralError.GENERAL_ERROR, "The processing key is required."),
    processing_value_required("422113", GeneralError.GENERAL_ERROR, "The processing value is required."),
    threeds_version_invalid("422114", GeneralError.GENERAL_ERROR, "The 3DS version is invalid."),
    threeds_version_not_supported("422115", GeneralError.GENERAL_ERROR, "The 3DS version is not supported."),

    //20x
    refer_to_card_issuer("20001", GeneralError.CONTACT_YOUR_BANK, "Refer to Card Issuer"),
    refer_to_card_issuer_special("20002", GeneralError.CONTACT_YOUR_BANK, "Refer to Card Issuer - Special Conditions"),
    invalid_merchant("20003", GeneralError.CONTACT_YOUR_BANK, "Invalid Merchant or Merchant is not active"),
    do_not_honour("20005", GeneralError.VERIFY_CARD_DETAILS, "Declined - Do Not Honour"),
    invalid_request("20006", GeneralError.CONTACT_YOUR_BANK, "Error / Invalid Request Parameters"),
    request_in_progress("20009", GeneralError.CONTACT_YOUR_BANK, "Request in Progress"),
    partial_value_approved("20010", GeneralError.CONTACT_YOUR_BANK, "Partial Value Approved"),
    invalid_transaction("20012", GeneralError.CONTACT_YOUR_BANK, "Invalid Transaction"),
    invalid_mount("20013", GeneralError.CONTACT_YOUR_BANK, "Invalid Value/Amount"),
    invalid_card_number("20014", GeneralError.VERIFY_CARD_DETAILS, "Invalid Card Number"),
    customer_cancel("20017", GeneralError.VERIFY_INPUT_DETAILS, "Customer Cancellation"),
    customer_dispute("20018", GeneralError.CONTACT_YOUR_BANK, "Customer Dispute"),
    transaction_expired("20019", GeneralError.TRY_AGAIN_LATER, "Re-enter Transaction or Transaction has expired"),
    invalid_response("20020", GeneralError.TRY_AGAIN_LATER, "Invalid Response"),
    no_action_taken("20021", GeneralError.TRY_AGAIN_LATER, "No Action Taken"),
    suspected_malfunction("20022", GeneralError.TRY_AGAIN_LATER, "Suspected Malfunction"),
    unacceptable_fee("20023", GeneralError.CONTACT_YOUR_BANK, "Unacceptable Transaction Fee"),
    file_update_not_supported("20024", GeneralError.CONTACT_YOUR_BANK, "File Update Not Supported by the Receiver"),
    unable_to_locate_record("20025", GeneralError.CONTACT_YOUR_BANK, "Unable to Locate Record on File"),
    duplicate_file_record("20026", GeneralError.CONTACT_YOUR_BANK, "Duplicate file update record"),
    file_update_field("20027", GeneralError.CONTACT_YOUR_BANK, "File Update Field Edit Error"),
    file_update_locked("20028", GeneralError.CONTACT_YOUR_BANK, "File Update File Locked Out"),
    file_update_not_success("20029", GeneralError.CONTACT_YOUR_BANK, "File Update not Successful"),
    format_error("20030", GeneralError.CONTACT_YOUR_BANK, "Format Error"),
    bank_not_supported("20031", GeneralError.CONTACT_YOUR_BANK, "Bank not Supported by Switch"),
    completed_partially("20032", GeneralError.CONTACT_YOUR_BANK, "Completed Partially"),
    no_credit_account("20039", GeneralError.CONTACT_YOUR_BANK, "No CREDIT Account"),
    function_not_supported("20040", GeneralError.CONTACT_YOUR_BANK, "Requested Function not Supported"),
    no_universal_amount("20042", GeneralError.CONTACT_YOUR_BANK, "No Universal Value/Amount"),
    no_investment_account("20044", GeneralError.CONTACT_YOUR_BANK, "No Investment Account"),
    bank_decline("20046", GeneralError.CONTACT_YOUR_BANK, "Bank Decline"),
    insufficient_funds("20051", GeneralError.INSUFFICIENT_FUNDS, "Insufficient Funds"),
    no_cheque_account("20052", GeneralError.CONTACT_YOUR_BANK, "No Cheque Account"),
    no_savings_account("20053", GeneralError.CONTACT_YOUR_BANK, "No Savings Account"),
    expired_card("20054", GeneralError.CONTACT_YOUR_BANK, "Expired Card"),
    incorrect_pin("20055", GeneralError.VERIFY_CARD_DETAILS, "Incorrect PIN (invalid Amex CVV)"),
    no_card_record("20056", GeneralError.CONTACT_YOUR_BANK, "No Card Record"),
    not_permitted_cardholder("20057", GeneralError.CONTACT_YOUR_BANK, "Transaction not Permitted to Cardholder"),
    not_permitted_terminal("20058", GeneralError.CONTACT_YOUR_BANK, "Transaction not Permitted to Terminal"),
    suspected_fraud("20059", GeneralError.CONTACT_YOUR_BANK, "Suspected Fraud"),
    contact_acquirer("20060", GeneralError.CONTACT_YOUR_BANK, "Card Acceptor Contact Acquirer"),
    limited_exceeded("20061", GeneralError.CONTACT_YOUR_BANK, "Activity Amount Limited Exceeded"),
    restricted_card("20062", GeneralError.CONTACT_YOUR_BANK, "Restricted Card"),
    security_violation("20063", GeneralError.CONTACT_YOUR_BANK, "Security Violation"),
    not_fulfil_aml("20064", GeneralError.TRY_AGAIN_LATER, "Transaction does not fulfil AML requirement"),
    withdrawal_limit("20065", GeneralError.CONTACT_YOUR_BANK, "Exceeds Withdrawal Frequency Limit"),
    acquirer_security_call("20066", GeneralError.CONTACT_YOUR_BANK, "Card Acceptor Call Acquirer Security"),
    up_card_at_atm("20067", GeneralError.CONTACT_YOUR_BANK, "Hard Capture—Pick Up Card at ATM"),
    timeout("20068", GeneralError.TRY_AGAIN_LATER, "Response Received Too Late / Timeout"),
    pin_tries_exceeded("20075", GeneralError.CONTACT_YOUR_BANK, "Allowable PIN Tries Exceeded"),
    no_security_model("20082", GeneralError.CONTACT_YOUR_BANK, "No security model"),
    no_accounts("20083", GeneralError.CONTACT_YOUR_BANK, "No accounts"),
    no_pbf("20084", GeneralError.CONTACT_YOUR_BANK, "No PBF"),
    pbf_update_error("20085", GeneralError.CONTACT_YOUR_BANK, "PBF update error"),
    atm_malfunction("20086", GeneralError.CONTACT_YOUR_BANK, "ATM Malfunction / Invalid authorisation type"),
    bad_track_data("20087", GeneralError.VERIFY_CARD_DETAILS, "Bad Track Data (invalid CVV and/or expiry date)"),
    unable_to_process("20088", GeneralError.CONTACT_YOUR_BANK, "Unable to Dispense/process"),
    administration_error("20089", GeneralError.TRY_AGAIN_LATER, "Administration Error"),
    cut_off_in_progress("20090", GeneralError.CONTACT_YOUR_BANK, "Cut-off in Progress"),
    issuer_inoperative("20091", GeneralError.TRY_AGAIN_LATER, "Issuer or Switch is Inoperative"),
    fin_institution_not_found("20092", GeneralError.TRY_AGAIN_LATER, "Financial Institution not Found"),
    violation_of_law("20093", GeneralError.CONTACT_YOUR_BANK, "Transaction cannot be completed; violation of law"),
    duplicate_invoice("20094", GeneralError.CONTACT_YOUR_BANK, "Duplicate Transmission/Invoice"),
    reconcile_error("20095", GeneralError.CONTACT_YOUR_BANK, "Reconcile Error"),
    system_malfunction("20096", GeneralError.CONTACT_YOUR_BANK, "System Malfunction"),
    reconciliation_reset("20097", GeneralError.CONTACT_YOUR_BANK, "Reconciliation Totals Reset"),
    mac_error("20098", GeneralError.CONTACT_YOUR_BANK, "MAC Error"),
    unidentified_responses("20099", GeneralError.GENERAL_ERROR, "Other / Unidentified responses"),
    unable_to_authorize("200N0", GeneralError.CONTACT_YOUR_BANK, "Unable to authorize"),
    decline_for_cvv_two_failure("200N7", GeneralError.VERIFY_CARD_DETAILS, "Decline for CVV2 failure"),
    pin_required("200O5", GeneralError.CONTACT_YOUR_BANK, "Pin Required"),
    over_daily_limit("200P1", GeneralError.LIMIT_EXCEEDED, "Over Daily Limit"),
    limit_exceeded("200P9", GeneralError.INSUFFICIENT_FUNDS, "Limit exceeded. Enter a lesser value."),
    issuer_stops_payment("200R1", GeneralError.TRY_AGAIN_LATER, "Issuer initiated a stop payment (revocation order) for the Authorization"),
    issuer_stops_payment_all("200R3", GeneralError.CONTACT_YOUR_BANK, "Issuer initiated a stop payment (revocation order) for all Authorizations"),
    ptlf_full("200S4", GeneralError.CONTACT_YOUR_BANK, "PTLF Full"),
    invalid_transaction_date("200T2", GeneralError.TRY_AGAIN_LATER, "Invalid Transaction Date"),
    card_not_supported("200T3", GeneralError.CONTACT_YOUR_BANK, "Card not supported"),
    wrong_caf_status("200T5", GeneralError.CONTACT_YOUR_BANK, "CAF Status=0 or 9"),
    invalid_date_format("20100", GeneralError.VERIFY_INPUT_DETAILS, "Invalid Expiry Date Format"),
    no_customer_token_invalid("20101", GeneralError.TRY_AGAIN_LATER, "No Account / No Customer (Token incorrect or invalid)"),
    invalid_merchant_id("20102", GeneralError.TRY_AGAIN_LATER, "Invalid Merchant/Wallet ID"),
    unsupported_card_type("20103", GeneralError.CONTACT_YOUR_BANK, "Card type/Payment method not supported"),
    gateway_reject_invalid_ransaction("20104", GeneralError.TRY_AGAIN_LATER, "Gateway Reject - Invalid Transaction"),
    Gateway_reject_violation("20105", GeneralError.TRY_AGAIN_LATER, "Gateway Reject - Violation"),
    unsupported_currency("20106", GeneralError.CONTACT_YOUR_BANK, "Unsupported currency"),
    no_billing_address("20107", GeneralError.TRY_AGAIN_LATER, "Billing address is missing"),
    cardholder_available("20108", GeneralError.CONTACT_YOUR_BANK, "Declined - Updated Cardholder Available"),
    authorization_already_reversed("20109", GeneralError.CONTACT_YOUR_BANK, "Authorization Already Reversed (voided) or Capture is larger than initial Authorised Value"),
    authorization_completed("20110", GeneralError.CONTACT_YOUR_BANK, "Authorization completed"),
    transaction_already_reversed("20111", GeneralError.CONTACT_YOUR_BANK, "Transaction already reversed"),
    mc_secure_code_enabled("20112", GeneralError.CONTACT_YOUR_BANK, "Merchant not Mastercard SecureCode enabled"),
    invalid_property("20113", GeneralError.TRY_AGAIN_LATER, "Invalid Property"),
    invalid_token("20114", GeneralError.TRY_AGAIN_LATER, "Invalid Channel or Token is incorrect"),
    invalid_lifetime("20115", GeneralError.TRY_AGAIN_LATER, "Missing/Invalid Lifetime"),
    invalid_encoding("20116", GeneralError.TRY_AGAIN_LATER, "Invalid Encoding"),
    invalid_api_version("20117", GeneralError.TRY_AGAIN_LATER, "Invalid API Version"),
    transaction_pending("20118", GeneralError.TRY_AGAIN_LATER, "Transaction Pending"),
    invalid_batch_data("20119", GeneralError.TRY_AGAIN_LATER, "Invalid Batch data and/or batch data is missing"),
    invalid_customer("20120", GeneralError.CONTACT_YOUR_BANK, "Invalid Customer/User"),
    merchant_limit_exceeded("20121", GeneralError.TRY_AGAIN_LATER, "Transaction Limit for Merchant/Terminal exceeded"),
    missing_basic_data("20123", GeneralError.CONTACT_YOUR_BANK, "MISSING BASIC DATA: zip, addr, member"),
    card_threed_secured("20150", GeneralError.CONTACT_YOUR_BANK, "Card not 3D Secure (3DS) enabled"),
    threed_failed("20151", GeneralError.GENERAL_ERROR, "Cardholder failed 3DS authentication"),
    threed_timeout("20152", GeneralError.TRY_AGAIN_LATER, "Initial 3DS transaction not completed within 15 minutes"),
    threed_system_malfunction("20153", GeneralError.TRY_AGAIN_LATER, "3DS system malfunction"),
    threed_required("20154", GeneralError.TRY_AGAIN_LATER, "3DS authentication required"),

    //since all codes are mapped to the single general error
    hard_decline("30xxx", GeneralError.CONTACT_YOUR_BANK, "Hard Decline"),
    risk_decline_settings("40101", GeneralError.CREDIT_CARD_PROHIBITED, "The request was blocked by your risk settings"),
    risk_decline("40xxx", GeneralError.ANOTHER_METHOD, "Risk Decline");

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

    public String getCheckoutError() { return this.code + ": " + this.description; }

    public GeneralError getGeneralError() { return this.generalError; }

    public String getGeneralErrorLocal(MessageSource messageSource, String domainName, String lang) {
        return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
    }


    public static CheckoutErrors fromErrorName(String name) {
        try {
             return CheckoutErrors.valueOf(name.replace("3ds", "threeds"));
        } catch (Exception e) {
            log.info("Unknown checkout error: " + name, e);
            return CheckoutErrors.unknown_error;
        }
    }

    @JsonCreator
    public static CheckoutErrors fromErrorCode(String errorCode) {
        if (errorCode != null && !errorCode.isEmpty()) {
            for (CheckoutErrors s : CheckoutErrors.values()) {
                if (errorCode.equalsIgnoreCase(s.getCode())) {
                    return s;
                }
            }
            if (errorCode.startsWith("3") && errorCode.length() == 5) {
                return CheckoutErrors.hard_decline;
            } else if (errorCode.startsWith("4") && errorCode.length() == 5) {
                return CheckoutErrors.risk_decline;
            }
        }
        return unknown_error;
    }
}
