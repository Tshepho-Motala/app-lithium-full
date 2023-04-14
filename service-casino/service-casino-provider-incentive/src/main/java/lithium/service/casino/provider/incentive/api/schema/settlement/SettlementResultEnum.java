package lithium.service.casino.provider.incentive.api.schema.settlement;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum SettlementResultEnum {
	WIN ("WIN"),
	LOST ("LOST"),
	VOID ("VOID");

	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;
	
	public String toString() {
		return value;
	}
}
