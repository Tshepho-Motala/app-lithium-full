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
public class Ruleset {
	/**
	 * Total transaction score for the ruleset.
	 */
	private Integer score;
	/**
	 * Number of rules that were triggered.
	 */
	private Integer rulesMatched;
	/**
	 * Separate entity with details on each rule that was triggered.
	 */
	private Rules[] rules;
}
