package lithium.service.cashier.client.frontend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
public enum ProcessorAccountResponseStatus {
	SUCCESS("SUCCESS"),
	FAILED("FAILED"),
	PENDING("PENDING"),
	CANCELED("CANCELED");

	@Setter
	@Accessors(fluent = true)
	private String status;

	@JsonValue
	public String status() {
		return status;
	}

	@JsonCreator
	public static ProcessorAccountResponseStatus fromStatus(String status) {
		for (ProcessorAccountResponseStatus ts : ProcessorAccountResponseStatus.values()) {
			if (ts.status.equalsIgnoreCase(status)) {
				return ts;
			}
		}
		return null;
	}

}
