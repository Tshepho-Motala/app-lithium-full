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
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Status {
	INITIATED("INITIATED"),
	PAYABLE("PAYABLE"),
	PROCESSING("PROCESSING"),
	FAILED("FAILED"),
	EXPIRED("EXPIRED"),
	COMPLETED("COMPLETED"),
	RECEIVED("RECEIVED"),
	HELD("HELD"),
	CANCELLED("CANCELLED"),
	PENDING("PENDING");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String status;

	@JsonCreator
	public static Status fromStatus(String status) {
		for (Status s: Status.values()) {
			if (s.status.equalsIgnoreCase(status)) {
				return s;
			}
		}
		return null;
	}
}
