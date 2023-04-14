package lithium.service.access.provider.iovation.data;

import java.util.UUID;

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
public class CheckTransactionDetailsResponse {
	/**
	 * The end user’s unique identifier, generally a username or ID. This value is echoed back from the request.
	 */
	private String accountCode;
	/**
	 * Structured JSON object that contains the device, IP and rules results.
	 */
	private Details details;
	/**
	 * A unique ID for the request.
	 */
	private UUID id;
	/**
	 * A user-specified value describing the rule that contributed the most to the result.
	 */
	private String reason;
	/**
	 * Result of the transaction risk or auth check, with a recommendation to allow (A), deny (D), or review (R) the transaction.
	 */
	private String result;
	/**
	 * A unique ID assigned to the transaction that can be used to locate the transaction in searches and reports. 
	 */
	private Long trackingNumber;
}
