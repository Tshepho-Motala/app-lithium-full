package lithium.service.cashier.processor.vespay.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum ValidationCallbackErrorCode {
	I000(000, "No corresponding error code"),
	I63(63, "ValidationForm shown"),
	I74(74, "Validation Declined SSN"),
	I94(94, "Validation Approved SSN"),
	I801(801, "Order blocked, Blacklisted Device"),
	I812(812, "Validation blocked, Max validation attempts"),
	I819(819, "Validation blocked, other reason"),
	I920(920, "Network Country not approved"),
	I921(921, "IP network type not approved");
	
	ValidationCallbackErrorCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static ValidationCallbackErrorCode find(final Integer code) {
		for (ValidationCallbackErrorCode ec: ValidationCallbackErrorCode.values()) {
			if (ec.getCode().equals(code)) return ec;
		}
		return I000;
	}
}