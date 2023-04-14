package lithium.service.cashier.enums;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.experimental.Accessors;

public enum CardType {
	UNKNOWN("", "UNKNOWN"),
	VISA("^4[0-9]{12}(?:[0-9]{3})?$", "VISA"),
	MASTERCARD("^5[1-5][0-9]{14}$", "MASTER"),
	AMERICAN_EXPRESS("^3[47][0-9]{13}$", "AMEX"),
	DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}$", "DINERS"),
	DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$", "DISCOVER"),
	JCB("^(?:2131|1800|35\\d{3})\\d{11}$", "JCB"),
	CHINA_UNION_PAY("^62[0-9]{14,17}$", "CUNION");
	
	private Pattern pattern;
	@Getter
	@Accessors(fluent=true)
	private String shortCode;
	
//	CardType() {
//		this.pattern = null;
//	}
	
	CardType(String pattern, String shortCode) {
		this.pattern = Pattern.compile(pattern);
		this.shortCode = shortCode;
	}
	
	public static CardType detect(String cardNumber) {
		for (CardType cardType:CardType.values()) {
			if (cardType.pattern == null) {
				continue;
			}
			if (cardType.pattern.matcher(cardNumber).matches()) {
				return cardType;
			}
		}
		
		return UNKNOWN;
	}
	
	public static boolean isValid(String cardNumber) {
		int sum = 0;
		boolean alternate = false;
		for (int i = cardNumber.length() - 1; i >= 0; i--) {
			int n = Integer.parseInt(cardNumber.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = (n % 10) + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}
		return (sum % 10 == 0);
	}

}