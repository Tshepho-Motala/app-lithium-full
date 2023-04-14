package lithium.service.cashier.processor.neteller.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum ExecutionMode {
	SYNCHRONOUS("SYNCHRONOUS");

	@Setter
	@Accessors(fluent=true)
	private String mode;

	@JsonCreator
	public static ExecutionMode fromMode(String mode) {
		for (ExecutionMode em: ExecutionMode.values()) {
			if (em.mode.equalsIgnoreCase(mode)) {
				return em;
			}
		}
		return null;
	}
}
