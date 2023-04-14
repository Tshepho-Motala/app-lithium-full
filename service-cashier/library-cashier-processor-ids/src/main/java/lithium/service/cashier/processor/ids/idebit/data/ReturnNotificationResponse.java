package lithium.service.cashier.processor.ids.idebit.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor

/**
 * Message is received from IDS when a transaction is reversed.
 * A response containing the original parameter map as a form post
 * should be sent to IDS once internal accounting is complete.
 */
public class ReturnNotificationResponse {

	private static final Map<String, String> ERROR_MAP = new HashMap<>();
	private static final Map<String, String> FI_ERROR_MAP = new HashMap<>();
	private String idsUserId;
	private String idsTransactionNumber;
	private String idsTransactionType;
	private String merchantId;
	private String userGuid;
	private String originalTransactionNumber;
	private String transactionDecimalAmount;
	private String transactionDecimalFee;
	private String currencyCode;
	private String idsOriginalTransactionNumber;
	private String errorCode;
	private String financialInstitutionReturnCode;

	private String errorString;
	private String financialInstitutionReturnCodeString;

	private Map<String, String> originalParameterMap;

	/**
	 * Populate error code lookups
	 */
	static {
		ERROR_MAP.put("1", "Failed IP validation");
		ERROR_MAP.put("2", "Consumer's IDS account is blocked or suspended");
		ERROR_MAP.put("3", "Suspicious or fraudulent payment history by the Consumer");
		ERROR_MAP.put("4", "Transaction amount exceeds the transaction limit");
		ERROR_MAP.put("7", "Maximum bank account verification attempts reached");
		ERROR_MAP.put("8", "The Consumer cancels the transaction after failed bank account verification");
		ERROR_MAP.put("9", "Maximum Consumer identity verification attempts reached");
		ERROR_MAP.put("10", "Bank account information mismatch");
		ERROR_MAP.put("12", "Consumer account temporarily blocked due to failed login attempts");
		ERROR_MAP.put("13", "Negative information on the bank account");
		ERROR_MAP.put("14", "Suspicious or negative information on the Consumer identity");
		ERROR_MAP.put("15", "Consumer's personal information mismatch");
		ERROR_MAP.put("16", "Insufficient funds to cover the transaction");
		ERROR_MAP.put("17", "Cross-currency is not supported");
		ERROR_MAP.put("19", "Consumer's country is blocked");
		ERROR_MAP.put("20", "Consumer must verify bank account");
		ERROR_MAP.put("22", "Suspicious or fraudulent information on the Consumer device / IPaddress");
		ERROR_MAP.put("24", "Consumer's country is not supported");
		ERROR_MAP.put("25", "Consumer's country is not supported by the Merchant (global transactions only)");
		ERROR_MAP.put("26", "Transaction abandoned by the Consumer (global transactions only)");
		ERROR_MAP.put("27", "Consumer using multiple accounts");
		ERROR_MAP.put("98", "Generic error");
		ERROR_MAP.put("99", "Consumer opt-out");

		FI_ERROR_MAP.put("00", "Edit Reject");
		FI_ERROR_MAP.put("01", "NSF (debit only)");
		FI_ERROR_MAP.put("02", "Account not found");
		FI_ERROR_MAP.put("03", "Payment stopped / recalled");
		FI_ERROR_MAP.put("04", "Post date or stale dated payment");
		FI_ERROR_MAP.put("05", "Account closed");
		FI_ERROR_MAP.put("06", "Account transferred");
		FI_ERROR_MAP.put("07", "No debit allowed");
		FI_ERROR_MAP.put("08", "Funds not cleared");
		FI_ERROR_MAP.put("09", "Currency or account mismatch");
		FI_ERROR_MAP.put("10", "Payor / payee deceased");
		FI_ERROR_MAP.put("11", "Account frozen");
		FI_ERROR_MAP.put("12", "Invalid or incorrect account number");
		FI_ERROR_MAP.put("14", "Incorrect payor / payee name");
		FI_ERROR_MAP.put("15", "No agreement existed");
		FI_ERROR_MAP.put("16", "Not in accordance with agreement - personal");
		FI_ERROR_MAP.put("17", "Agreement revoked - personal");
		FI_ERROR_MAP.put("18", "No confirmation or prenotification - personal");
		FI_ERROR_MAP.put("19", "Not in accordance with agreement - business");
		FI_ERROR_MAP.put("20", "Agreement revoked - business");
		FI_ERROR_MAP.put("21", "No pre-notification - business");
		FI_ERROR_MAP.put("22", "Consumer initiated return - credit only");
		FI_ERROR_MAP.put("80", "Payment recalled");
		FI_ERROR_MAP.put("81", "Interbank reject - invalid due date");
		FI_ERROR_MAP.put("82", "Interbank reject - invalid institution number");
		FI_ERROR_MAP.put("83", "Interbank reject - invalid account number");
		FI_ERROR_MAP.put("84", "Interbank reject - invalid institution and account number");
		FI_ERROR_MAP.put("85", "Interbank reject - other");
		FI_ERROR_MAP.put("96", "Chargeback");
		FI_ERROR_MAP.put("98", "No agreement for returns");
	}
	/**
	 * Assign post parameter map to local variable and populate the object fields
	 * @param pMap form post parameters
	 */
	public void mapParametersToObject(Map<String, String> pMap) {
		originalParameterMap = pMap;
		setIdsUserId(pMap.get("user_id"));
		setIdsTransactionNumber(pMap.get("txn_num"));
		setIdsTransactionType(pMap.get("txn_type"));
		setMerchantId(pMap.get("merchant_id"));
		setUserGuid(pMap.get("merchant_user_id"));
		setOriginalTransactionNumber(pMap.get("merchant_txn_num"));
		setTransactionDecimalAmount(pMap.get("txn_amount"));
		setTransactionDecimalFee(pMap.get("txn_fee"));
		setCurrencyCode(pMap.get("txn_currency"));
		setIdsOriginalTransactionNumber(pMap.get("original_txn_num"));
		setErrorCode(pMap.get("error_code"));
		setFinancialInstitutionReturnCode("return_code");

		if (getErrorCode() != null) {
			setErrorString(ERROR_MAP.get(getErrorCode()));
		}

		if (getFinancialInstitutionReturnCode() != null) {
			setFinancialInstitutionReturnCodeString(FI_ERROR_MAP.get(getFinancialInstitutionReturnCode()));
		}
	}




}
