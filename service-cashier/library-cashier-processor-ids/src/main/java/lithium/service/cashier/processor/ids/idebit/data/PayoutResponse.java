package lithium.service.cashier.processor.ids.idebit.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor

/**
 * Message is received from IDS when a payment is processed.
 */
public class PayoutResponse {

	private static final Map<String, String> ERROR_MAP = new HashMap<>();
	private String idsTransactionNumber;
	private String transactionDecimalFee;
	private String status;
	private String errorCode;
	private String errorString;

	static {
		ERROR_MAP.put("F002", "Content of the request doesn't comply with the specification.");
		ERROR_MAP.put("F003", "The Merchant failed IDS authentication.");
		ERROR_MAP.put("F004", "The user ID provided by the Merchant doesn't exist in the IDS system.");
		ERROR_MAP.put("F005", "The Merchant user ID field is missing.");
		ERROR_MAP.put("F006", "The transaction type is incorrect.");
		ERROR_MAP.put("F007", "The transaction amount is incorrect.");
		ERROR_MAP.put("F008", "The transaction currency isn't supported.");
		ERROR_MAP.put("F009", "The Merchant transaction number isn't unique.");
		ERROR_MAP.put("F010", "System internal error.");
		ERROR_MAP.put("F011", "System internal error.");
		ERROR_MAP.put("F012", "System internal error.");
		ERROR_MAP.put("F013", "System internal error.");
		ERROR_MAP.put("F014", "System internal error.");
		ERROR_MAP.put("F015", "System internal error.");
		ERROR_MAP.put("F016", "The Consumer has been suspended or blocked by IDS for suspicious or fraudulent activities.");
		ERROR_MAP.put("F017", "The transaction is declined to avoid overdraft from the Merchant account. \nThe Merchant's IDS account balance must be sufficient to cover the payout amount.");
	}
	/**
	 * Assign post parameter map to local variable and populate the object fields
	 * @param pMap form post parameters
	 */
	public void mapParametersToObject(Map<String, String> pMap) {
		setIdsTransactionNumber(pMap.get("txn_num"));
		setTransactionDecimalFee(pMap.get("txn_fee"));
		setStatus(pMap.get("txn_status"));
		setErrorCode(pMap.get("error_code"));

		if (getErrorCode() != null) {
			setErrorString(ERROR_MAP.get(getErrorCode()));
		}
	}




}
