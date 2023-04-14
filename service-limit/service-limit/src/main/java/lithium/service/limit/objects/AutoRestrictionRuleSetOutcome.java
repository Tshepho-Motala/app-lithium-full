package lithium.service.limit.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AutoRestrictionRuleSetOutcome {
	private Integer id;
	private String outcome;
	private String displayName;
}
