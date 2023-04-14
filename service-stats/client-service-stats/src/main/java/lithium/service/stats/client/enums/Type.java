package lithium.service.stats.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Type {
	USER("user"),
	RAF("raf"),
	CASHIER("cashier");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String type;

	@JsonCreator
	public static Type fromType(String type) {
		for (Type t: Type.values()) {
			if (t.type.equalsIgnoreCase(type)) {
				return t;
			}
		}
		return null;
	}
}