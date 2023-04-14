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
public enum PaymentType {
	NETELLER("NETELLER"),
	CARD("CARD"),
	SIGHTLINE("SIGHTLINE"),
	SKRILL("SKRILL"),
	VIPPREFERRED("VIPPREFERRED");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String type;

	@JsonCreator
	public static PaymentType fromType(String type) {
		for (PaymentType t: PaymentType.values()) {
			if (t.type.equalsIgnoreCase(type)) {
				return t;
			}
		}
		return null;
	}
}
