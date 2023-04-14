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
public enum Action {
	REDIRECT("REDIRECT");

	@Setter
	@Accessors(fluent=true)
	private String action;

	@JsonCreator
	public static Action fromAction(String action) {
		for (Action a: Action.values()) {
			if (a.action.equalsIgnoreCase(action)) {
				return a;
			}
		}
		return null;
	}
}
