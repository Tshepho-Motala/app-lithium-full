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
public enum Rel {
	DEFAULT("DEFAULT"),
	ON_COMPLETED("ON_COMPLETED"),
	ON_FAILED("ON_FAILED"),
	PAYMENT_REDIRECT("PAYMENT_REDIRECT");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String rel;

	@JsonCreator
	public static Rel fromRel(String rel) {
		for (Rel r: Rel.values()) {
			if (r.rel.equalsIgnoreCase(rel)) {
				return r;
			}
		}
		return null;
	}
}
