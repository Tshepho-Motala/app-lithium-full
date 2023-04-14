package lithium.service.sms.provider.clickatell.data.enums;

import lombok.Getter;

public enum MessageStatusCode {
	I001("001", "Message unknown"),
	I002("002", "Message queued"),
	I003("003", "Delivered to gateway"),
	I004("004", "Received by recipient"),
	I005("005", "Error with message"),
	I006("006", "User cancelled message delivery"),
	I007("007", "Error delivering message"),
	I009("009", "Routing error"),
	I010("010", "Message expired"),
	I011("011", "Message scheduled for later delivery"),
	I012("012", "Out of credit"),
	I013("013", "Clickatell cancelled message delivery"),
	I014("014", "Maximum MT limit exceeded"),
	I000("000", "No valid status code returned");
	
	MessageStatusCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private String code;
	@Getter
	private String description;
	
	public static MessageStatusCode find(final String code) {
		for (MessageStatusCode msc: MessageStatusCode.values()) {
			if (msc.getCode().equals(code)) return msc;
		}
		return I000;
	}
}