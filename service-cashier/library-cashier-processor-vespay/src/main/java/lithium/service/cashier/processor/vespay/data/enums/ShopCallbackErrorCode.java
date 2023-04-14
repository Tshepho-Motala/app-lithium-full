package lithium.service.cashier.processor.vespay.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum ShopCallbackErrorCode {
	I000(000, "No corresponding error code"),
	I103(103, "Checkout Page shown"),
	I104(104, "Customer cancel"),
	I105(105, "Wrong CC values"),
	I106(106, "Wrong Card Brand, not Visa or MC"),
	I107(107, "Unauthorised Card type"),
	I120(120, "Velocity on Email"),
	I121(121, "Velocity on ip"),
	I122(122, "Velocity on device"),
	I123(123, "Velocity on card"),
	I124(124, "Card used by more then on email"),
	I203(203, "PSP Declined, denied by Issuer"),
	I204(204, "PSP Declined, Insufficient Funds"),
	I205(205, "PSP Declined, Invalid 3DS authentication"),
	I206(206, "PSP Declined, Card Info wrong"),
	I207(207, "PSP Declined, Stolen Card"),
	I208(208, "PSP Declined, Retain Card"),
	I209(209, "PSP Declined, 3DS is not enabled on card"),
	I210(210, "PSP Declined, Suspicion of fraud"),
	I211(211, "PSP Authentication failed. Please retry or cance"),
	I212(212, "PSP, Canceled by cardholder"),
	I213(213, "PSP Declined, maximum time allowed has elapsed"),
	I214(214, "PSP Declined. A technical problem has occurred. P"),
	I215(215, "PSP Declined, Restricted Card"),
	I216(216, "PSP Declined, Issuing Bank is temporarily unavailable"),
	I217(217, "PSP Declined, Amount exceeds card limit, contact card issuer"),
	I280(280, "PSP Blocked, Velocity on email"),
	I281(281, "PSP Blocked, Velocity on ip"),
	I282(282, "PSP Blocked, Velocity on card"),
	I296(296, "PSP Error, PSP system level"),
	I297(297, "PSP Error, Integration level"),
	I298(298, "PSP Declined, Other Error"),
	I299(299, "PSP, Declined, unknown PSP Error"),
	I801(801, "Order blocked, Blacklisted Device"),
	I802(802, "Order blocked, Blacklisted CIDR"),
	I803(803, "Order blocked, Blacklisted IP"),
	I804(804, "Order blocked, Blacklisted Email"),
	I805(805, "Order blocked, Blacklisted Card BIN"),
	I806(806, "Order blocked, Blacklisted Card"),
	I820(820, "Client arrived to Expired Transaction (TTL)"),
	I829(829, "Transaction blocked, other reason"),
	I920(920, "Network Country not approved"),
	I921(921, "IP network type not approved");
	
	ShopCallbackErrorCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static ShopCallbackErrorCode find(final Integer code) {
		for (ShopCallbackErrorCode ec: ShopCallbackErrorCode.values()) {
			if (ec.getCode().equals(code)) return ec;
		}
		return I000;
	}
}