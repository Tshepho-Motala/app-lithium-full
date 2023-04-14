package lithium.service.cashier.processor.btc.clearcollect.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RefundType implements Serializable {
	
	REGULAR("R"),
	EXPRESS("X");
	
	@Getter
	private String code;
	
	public static RefundType fromCode(String code) {
		for (RefundType t: RefundType.values()) {
			if (t.getCode().equals(code)) return t;
		}
		return null;
	}
}