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
public enum TransactionType {
	PAYMENT("PAYMENT"),
	STANDALONE_CREDIT("STANDALONE_CREDIT");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String type;

	@JsonCreator
	public static TransactionType fromType(String type) {
		for (TransactionType tt: TransactionType.values()) {
			if (tt.type.equalsIgnoreCase(type)) {
				return tt;
			}
		}
		return null;
	}
}