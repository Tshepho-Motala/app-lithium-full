package lithium.service.cashier.method.premierpay.enums;

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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Status {
	APPROVED("APPROVED"),
	DECLINED("DECLINED"),
	ERROR("ERROR"),
	REFUNDED("REFUNDED");
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String code;
	
	@JsonCreator
	public static Status fromCode(String code) {
		for (Status ec : Status.values()) {
			if (ec.code.equalsIgnoreCase(code)) {
				return ec;
			}
		}
		return null;
	}
}