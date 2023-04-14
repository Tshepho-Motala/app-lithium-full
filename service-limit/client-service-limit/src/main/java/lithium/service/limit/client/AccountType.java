package lithium.service.limit.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum AccountType {
	PLAYER_BALANCE ("PLAYER_BALANCE");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;

	public String toString() {
		return value;
	}
}
