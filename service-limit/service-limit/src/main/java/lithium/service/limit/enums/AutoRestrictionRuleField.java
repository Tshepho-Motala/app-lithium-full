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
public enum AutoRestrictionRuleField {
	DAYS_SINCE_REGISTRATION(0, "DAYS_SINCE_REGISTRATION", "long"),
	VERIFICATION_STATUS(1, "VERIFICATION_STATUS", "csv"),
	LT_DEPOSITS_IN_CENTS(2, "LT_DEPOSITS_IN_CENTS", "long"),
	LT_WITHDRAWALS_IN_CENTS(3, "LT_WITHDRAWALS_IN_CENTS", "long"),
	CONTRA_PAYMENT_ACCOUNT_SET(4, "CONTRA_PAYMENT_ACCOUNT_SET", "boolean"),
	HOURS_SINCE_1ST_DEPOSIT(5, "HOURS_SINCE_1ST_DEPOSIT", "long"),
	SPECIFIC_RESTRICTION_EVENT(6,  "SPECIFIC_RESTRICTION_EVENT", "boolean"),
	RESTRICTION_SUB_TYPE(7,  "RESTRICTION_SUB_TYPE", "long"),
	DAYS_SINCE_RESTRICTION_ACTIVE(8,  "DAYS_SINCE_RESTRICTION_ACTIVE", "long"),
	AGE(9, "AGE", "long"),
	USER_STATUS_IS_USER_ENABLED(10, "USER_STATUS_IS_USER_ENABLED", "boolean");

	@Setter
	@Accessors(fluent=true)
	private Integer id;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String field;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String type;
	
	@JsonValue
	public Integer id() {
		return id;
	}

	public static AutoRestrictionRuleField fromField(String field) {
		for (AutoRestrictionRuleField a: AutoRestrictionRuleField.values()) {
			if (a.field.equalsIgnoreCase(field)) {
				return a;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static AutoRestrictionRuleField fromId(Integer id) {
		for (AutoRestrictionRuleField o: AutoRestrictionRuleField.values()) {
			if (o.id.compareTo(id) == 0) {
				return o;
			}
		}
		return null;
	}
}
