package lithium.service.client.objects;

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
 * This enum has existed in many shapes and forms in various places. Was started in service-accounting,
 * and has been copied to many places. It should however live in a common place.
 * Other places where this is found should be marked as @deprecated.
 *
 * @version 1.0
 * @since   2020-04-25
 */

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Granularity {
	GRANULARITY_YEAR(1, "GRANULARITY_YEAR"),
	GRANULARITY_MONTH(2, "GRANULARITY_MONTH"),
	GRANULARITY_DAY(3, "GRANULARITY_DAY"),
	GRANULARITY_WEEK(4, "GRANULARITY_WEEK"),
	GRANULARITY_TOTAL(5, "GRANULARITY_TOTAL"),
	GRANULARITY_HOUR(6, "GRANULARITY_HOUR");

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