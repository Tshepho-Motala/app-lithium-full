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
public class RegistrationResult {
	/**
	 * Whether the device is registered to an account.
	 */
	private String matchStatus;
	/**
	 * When the matchStatus attribute returns a MATCH result, measureOfChange attribute returns an assessment
	 * of the degree of difference between the device from the new transaction and the closest matching device
	 * that is already paired with the account.
	 */
	private String measureOfChange;
}
