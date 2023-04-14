package lithium.service.cashier.processor.cc.trustspay.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderStatus implements Serializable {

	CONFIRMING(-2, "Confirming"),
	PROCESSING(-1, "Processing"),
	FAILED(0, "Failed"),
	SUCCESS(1, "Success");
	
	public static OrderStatus fromCode(int code) {
		for (OrderStatus r: OrderStatus.values()) {
			if (r.getCode() == code) return r;
		}
		return null;
	}

	@Getter
	private Integer code;
	@Getter
	private String description;

}