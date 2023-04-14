package lithium.service.cashier.processor.btc.clearcollect.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AgreeRiskType implements Serializable {
	
	YES("Y"),
	NO("N");
	
	@Getter
	private String code;
	
	public static AgreeRiskType fromCode(String code) {
		for (AgreeRiskType t: AgreeRiskType.values()) {
			if (t.getCode().equals(code)) return t;
		}
		return null;
	}
}