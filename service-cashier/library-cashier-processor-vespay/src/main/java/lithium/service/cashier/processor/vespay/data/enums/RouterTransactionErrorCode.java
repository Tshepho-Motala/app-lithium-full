package lithium.service.cashier.processor.vespay.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum RouterTransactionErrorCode {
	I000(000, "No corresponding error code"),
	I120(120, "Velocity on Email"),
	I121(121, "Velocity on ip"),
	I122(122, "Velocity on device"),
	I801(801, "Transaction blocked, Blacklisted Device"),
	I802(802, "Transaction blocked, Blacklisted CIDR"),
	I803(803, "Transaction blocked, Blacklisted IP"),
	I804(804, "Transaction blocked, Blacklisted Email"),
	I901(901, "Wrong ApiKey"),
	I902(902, "Reached projected 24H limit on Contract"),
	I903(903, "Merchant not Active"),
	I904(904, "Values missing"),
	I905(905, "Values wrong format"),
	I906(906, "Minimum conditions on profile or age"),
	I920(920, "Network Country not approved"),
	I921(921, "IP network type not approved"),
	I998(998, "Temporarily disabled for system update"),
	I999(999, "Other API Error");
	
	RouterTransactionErrorCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static RouterTransactionErrorCode find(final Integer code) {
		for (RouterTransactionErrorCode ec: RouterTransactionErrorCode.values()) {
			if (ec.getCode().equals(code)) return ec;
		}
		return I000;
	}
}