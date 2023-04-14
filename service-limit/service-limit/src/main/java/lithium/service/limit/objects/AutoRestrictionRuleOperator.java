package lithium.service.limit.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AutoRestrictionRuleOperator {
	private Integer id;
	private String operator;
	private String displayName;
}
