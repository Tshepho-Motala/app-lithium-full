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
public enum AutoRestrictionRuleSetOutcome {
	PLACE(0, "PLACE"),
	LIFT(1, "LIFT");
	
	@Setter
	@Accessors(fluent=true)
	private Integer id;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String outcome;
	
	@JsonValue
	public Integer id() {
		return id;
	}

	public static AutoRestrictionRuleSetOutcome fromOutcome(String outcome) {
		for (AutoRestrictionRuleSetOutcome a: AutoRestrictionRuleSetOutcome.values()) {
			if (a.outcome.equalsIgnoreCase(outcome)) {
				return a;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static AutoRestrictionRuleSetOutcome fromId(Integer id) {
		for (AutoRestrictionRuleSetOutcome o: AutoRestrictionRuleSetOutcome.values()) {
			if (o.id.compareTo(id) == 0) {
				return o;
			}
		}
		return null;
	}
}