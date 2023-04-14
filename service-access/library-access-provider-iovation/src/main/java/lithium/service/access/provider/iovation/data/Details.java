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
public class Details {
	/**
	 * Device details captured by the iovation device recognition process.
	 */
	private Device device;
	/**
	 * SureScore results. This is only returned if you have subscribed to iovation SureScore.
	 */
	private MachineLearning machineLearning;
	/**
	 * IP properties for the Real IP address.
	 */
	private IP realIp;
	/**
	 * Ruleset and specific rule results, including scores.
	 */
	private Ruleset ruleset;
	/**
	 * IP properties for the end-user's stated IP address.
	 */
	private IP statedIp;
}
