package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum AutoRestrictionRuleOperator {
	BETWEEN(0, "BETWEEN"),
	EQUALS(1, "EQUALS"),
	GREATER_THAN(2, "GREATER_THAN"),
	GREATER_THAN_OR_EQUALS(3, "GREATER_THAN_OR_EQUALS"),
	LESS_THAN(4, "LESS_THAN"),
	LESS_THAN_OR_EQUALS(5, "LESS_THAN_OR_EQUALS"),
	IN(6, "IN");
	
	@Setter
	@Accessors(fluent=true)
	private Integer id;
	
	@Getter
	@Setter
	@Accessors(fluent=true)
	private String operator;
	
	@JsonValue
	public Integer id() {
		return id;
	}

	public static AutoRestrictionRuleOperator fromOperator(String operator) {
		for (AutoRestrictionRuleOperator o: AutoRestrictionRuleOperator.values()) {
			if (o.operator.equalsIgnoreCase(operator)) {
				return o;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static AutoRestrictionRuleOperator fromId(Integer id) {
		for (AutoRestrictionRuleOperator o: AutoRestrictionRuleOperator.values()) {
			if (o.id.compareTo(id) == 0) {
				return o;
			}
		}
		return null;
	}
}