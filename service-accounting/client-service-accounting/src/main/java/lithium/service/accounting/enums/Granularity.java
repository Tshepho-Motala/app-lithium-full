package lithium.service.accounting.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Granularity Enum
 * <p>
 * Yep, another way to identify granularity.  What makes this implementation better to use,
 * is the type safe way of doing "the for loop"  to get all granularities:
 * 
 * <pre>
 * for (Granularity granularity : Granularity.values()) 
 * </pre>
 * vs
 * <pre>
 * for (int g  = 1; g <= 5; g++)
 * </pre>
 *  
 * Thus, if we move all for loops over to this format, when we one day want to add hourly we will
 * have it clearly identified.
 *  
 * @author johantheitguy
 *
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public enum Granularity {
	
	GRANULARITY_YEAR(1, "GRANULARITY_YEAR"),
	GRANULARITY_MONTH(2, "GRANULARITY_MONTH"),
	GRANULARITY_DAY(3, "GRANULARITY_DAY"),
	GRANULARITY_WEEK(4, "GRANULARITY_WEEK"),
	GRANULARITY_TOTAL(5, "GRANULARITY_TOTAL");
	// Not yet implemented, but want to!
	// GRANULARITY_HOUR(6, "GRANULARITY_HOUR");
	
	@Setter
	@Accessors(fluent = true)
	private Integer id;
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String type;
	
	@JsonValue
	public Integer id() {
		return id;
	}
	
	@JsonCreator
	public static Granularity fromId(int id) {
		for (Granularity g : Granularity.values()) {
			if (g.id == id) {
				return g;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static Granularity fromType(String type) {
		for (Granularity g : Granularity.values()) {
			if (g.type.equalsIgnoreCase(type)) {
				return g;
			}
		}
		return null;
	}
	
}