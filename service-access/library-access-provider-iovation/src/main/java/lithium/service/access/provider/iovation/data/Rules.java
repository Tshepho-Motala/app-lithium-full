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
public class Rules {
	/**
	 * Type of rule that triggered.
	 */
	private String type;
	/**
	 * The reason associated with the rule that matched. This value is supplied by you.
	 */
	private String reason;
	/**
	 * Score contribution from the rule.
	 */
	private Integer score;
}
