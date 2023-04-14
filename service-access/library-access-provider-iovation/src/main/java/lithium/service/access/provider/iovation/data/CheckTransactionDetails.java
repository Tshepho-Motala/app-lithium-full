package lithium.service.access.provider.iovation.data;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransactionDetails {
	/**
	 * Unique identifier for the end-user's account, or for the transaction.
	 */
	private String accountCode;
	/**
	 * The blackbox, which is an encoded string that includes all of the device information that iovation collects.
	 */
	private String blackbox;
	/**
	 * The stated IP address from the end-user's device.
	 */
	private String statedIp;
	/**
	 * Transaction attributes that you can optionally send along with device information, that enable you to search for fraud based on identity data.
	 */
	private Map<String, Object> transactionInsight;
	/**
	 * The identifier for the rule set to use for the transaction.
	 */
	private String type;
}
