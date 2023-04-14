package lithium.service.cashier.processor.vespay.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum VoucherCallbackErrorCode {
	I000(000, "No corresponding error code"),
	I490(490, "Voucher refunded, Merchant request"),
	I491(491, "CB Prevention by staff decision, is refunded"),
	I492(492, "CB Prevention, related Vouchers is refunded"),
	I493(493, "Chargeback, not refunded"),
	I494(494, "Fraud alert, Ethoca, is refunded"),
	I495(495, "Fraud alert, CDRN, is refunded"),
	I496(496, "CB prevention, that is refunded, receives CB");
	
	VoucherCallbackErrorCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static VoucherCallbackErrorCode find(final Integer code) {
		for (VoucherCallbackErrorCode ec: VoucherCallbackErrorCode.values()) {
			if (ec.getCode().equals(code)) return ec;
		}
		return I000;
	}
}