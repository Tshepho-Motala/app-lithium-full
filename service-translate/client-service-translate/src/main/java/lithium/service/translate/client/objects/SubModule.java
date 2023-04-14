package lithium.service.translate.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum SubModule {
	REGISTRATION("REGISTRATION"),
	LOGIN("LOGIN"),
	CASHIER("CASHIER"),
	PASSWORD("PASSWORD"),
	MY_ACCOUNT("MY_ACCOUNT"),
	NORMAL_RESTRICTION("NORMAL_RESTRICTION"),
	SYSTEM_RESTRICTION("SYSTEM_RESTRICTION"),
	LIMIT_SYSTEM_ACCESS("LIMIT_SYSTEM_ACCESS"),
	GAMES("GAMES");

	@Setter
	@Accessors(fluent = true)
	private String name;

	@JsonCreator
	public static SubModule fromName(String name) {
		for (SubModule c : SubModule.values()) {
			if (c.name.equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
}
