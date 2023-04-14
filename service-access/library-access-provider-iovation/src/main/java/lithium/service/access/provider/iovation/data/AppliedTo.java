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
public class AppliedTo {
	/**
	 * Required
	 * Defines the type of entity, either accounts or devices.
	 */
	private String type;
	/**
	 * Required if type = account
	 * Account code to apply the evidence to.
	 */
	private String accountCode;
	/**
	 * Required if type = device
	 * Device ID to apply the evidence to.
	 */
	private String deviceAlias;
}
