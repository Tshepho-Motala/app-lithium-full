package lithium.service.cashier.processor.vespay.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum VoucherRedemptionErrorCode {
	I000(000, "No corresponding error code"),
	I406(406, "Voucher redemption denied, used"),
	I407(407, "Voucher redemption denied, refunded"),
	I408(408, "Voucher redemption denied, invalid voucher code"),
	I409(499, "Other Voucher Error"),
	I801(801, "Voucher blocked, Blacklisted Device"),
	I802(802, "Voucher blocked, Blacklisted CIDR"),
	I803(803, "Voucher blocked, Blacklisted IP"),
	I804(804, "Voucher blocked, Blacklisted Email"),
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
	
	VoucherRedemptionErrorCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static VoucherRedemptionErrorCode find(final Integer code) {
		for (VoucherRedemptionErrorCode ec: VoucherRedemptionErrorCode.values()) {
			if (ec.getCode().equals(code)) return ec;
		}
		return I000;
	}
}