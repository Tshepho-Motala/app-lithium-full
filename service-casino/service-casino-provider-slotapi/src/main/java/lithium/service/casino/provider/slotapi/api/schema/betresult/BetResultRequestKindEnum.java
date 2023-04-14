package lithium.service.casino.provider.slotapi.api.schema.betresult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum BetResultRequestKindEnum {
	WIN ("WIN"),
	LOSS ("LOSS"),
	VOID ("VOID"),
	FREE_WIN ("FREE_WIN"),
	FREE_LOSS ("FREE_LOSS");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;
	
	public String toString() {
		return value;
	}
}
