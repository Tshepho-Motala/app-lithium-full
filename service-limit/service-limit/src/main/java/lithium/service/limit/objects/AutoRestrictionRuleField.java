package lithium.service.limit.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AutoRestrictionRuleField {
	private Integer id;
	private String field;
	private String displayName;

}
