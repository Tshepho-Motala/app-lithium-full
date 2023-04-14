package lithium.service.cashier.processor.cc.qwipi.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResultCode implements Serializable {
	
	SUCCESS(0, "Success"),
	FAILED(1, "Failed"),
	PROCESSING(2, "Processing");
	
	public static ResultCode fromCode(int code) {
		for (ResultCode r: ResultCode.values()) {
			if (r.getCode() == code) return r;
		}
		return null;
	}

	@Getter
	private Integer code;
	@Getter
	private String description;

}