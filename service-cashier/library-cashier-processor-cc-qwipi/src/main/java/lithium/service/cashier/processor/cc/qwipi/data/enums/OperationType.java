package lithium.service.cashier.processor.cc.qwipi.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OperationType implements Serializable {
	
	PAYMENT(1, "01"),
	REFUND(2, "02");
	
	@Getter
	private Integer code;
	@Getter
	private String codeString;
	
	public static OperationType fromCodeString(String codeString) {
		for (OperationType t: OperationType.values()) {
			if (t.getCodeString().equals(codeString)) return t;
		}
		return null;
	}

}