package lithium.service.casino.client.objects;

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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public enum Granularity {
	GRANULARITY_YEAR(1, "GRANULARITY_YEAR"),
	GRANULARITY_MONTH(2, "GRANULARITY_MONTH"),
	GRANULARITY_DAY(3, "GRANULARITY_DAY"),
	GRANULARITY_WEEK(4, "GRANULARITY_WEEK"),
	GRANULARITY_TOTAL(5, "GRANULARITY_TOTAL"),
	GRANULARITY_HOUR(6, "GRANULARITY_HOUR");
//	GRANULARITY_MINUTE(7, "GRANULARITY_MINUTE");
	
	@Setter
	@Accessors(fluent = true)
	private Integer granularity;
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String type;
	
	@JsonValue
	public Integer granularity() {
		return granularity;
	}
	
	@JsonCreator
	public static Granularity fromGranularity(int granularity) {
		for (Granularity g : Granularity.values()) {
			if (g.granularity == granularity) {
				return g;
			}
		}
		return GRANULARITY_TOTAL;
	}
	
	@JsonCreator
	public static Granularity fromType(String type) {
		for (Granularity g : Granularity.values()) {
			if (g.type.equalsIgnoreCase(type)) {
				return g;
			}
		}
		return GRANULARITY_TOTAL;
	}
}