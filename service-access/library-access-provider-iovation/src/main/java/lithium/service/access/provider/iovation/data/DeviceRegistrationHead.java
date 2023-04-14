package lithium.service.access.provider.iovation.data;

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
public class DeviceRegistrationHead {
	/**
	 * Type of API response, such as, AddRegistrationResult, or
	 * CheckRegistrationResult, ResetPairingResult
	 */
	private String responseType;
	
	/**
	 * HTTP status code for the response, such as 200
	 */
	private int status;

	/**
	 * Unique iovation transaction identifier
	 */
	private String transactionId;

	/**
	 * The response status, such as success
	 */
	private String result;

	/**
	 * The API version
	 */
	private double version;
}
