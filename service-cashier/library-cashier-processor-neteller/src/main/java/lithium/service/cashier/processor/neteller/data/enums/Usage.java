package lithium.service.cashier.processor.neteller.data.enums;

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
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum Usage {
	SIMGLE_USE("SINGLE_USE"),
	MULTI_USE("MULTI_USE");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String usage;

	@JsonCreator
	public static Usage fromUsage(String usage) {
		for (Usage u: Usage.values()) {
			if (u.usage.equalsIgnoreCase(usage)) {
				return u;
			}
		}
		return null;
	}
}
