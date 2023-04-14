package lithium.service.cashier.processor.neteller.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Currency {
	CURRENCY_AED("AED", "Emirati Dirham", 2),
	CURRENCY_AUD("AUD", "Australian Dollar", 2),
	CURRENCY_BRL("BRL", "Brazilian Real", 2),
	CURRENCY_GBP("GBP", "British Pound", 2),
	CURRENCY_BGN("BGN", "Bulgarian Lev", 2),
	CURRENCY_CAD("CAD", "Canadian Dollar", 2),
	CURRENCY_COP("COP", "Colombian Peso", 2),
	CURRENCY_CNY("CNY", "Chinese Yuan Renminbi", 2),
	CURRENCY_DKK("DKK", "Danish Krone", 2),
	CURRENCY_EUR("EUR", "Euro", 2),
	CURRENCY_HUF("HUF", "Hungarian Forint", 0),
	CURRENCY_INR("INR", "Indian Rupee", 2),
	CURRENCY_JPY("JPY", "Japanese Yen", 0),
	CURRENCY_MYR("MYR", "Malaysian Ringgit", 2),
	CURRENCY_MAD("MAD", "Moroccan Dirham", 2),
	CURRENCY_MXN("MXN", "Mexican Peso", 2),
	CURRENCY_NGN("NGN", "Nigerian Naira", 2),
	CURRENCY_NOK("NOK", "Norwegian Kroner", 2),
	CURRENCY_PLN("PLN", "Polish Zloty", 2),
	CURRENCY_RON("RON", "Romanian New Leu", 2),
	CURRENCY_RUB("RUB", "Russian Ruble", 2),
	CURRENCY_SGD("SGD", "Singapore Dollar", 2),
	CURRENCY_SEK("SEK", "Swedish Krona", 2),
	CURRENCY_CHF("CHF", "Swiss Franc", 2),
	CURRENCY_TWD("TWD", "Taiwan New Dollar", 2),
	CURRENCY_TND("TND", "Tunisia Dinar", 3),
	CURRENCY_USD("USD", "United States Dollar", 2),
	CURRENCY_ZAR("ZAR", "South African Rand", 2);
	
	@Getter
	@Setter
	@Accessors(fluent=true)
	private String code;
	@Getter
	@Setter
	@Accessors(fluent=true)
	private String description;
	@Getter
	@Setter
	@Accessors(fluent=true)
	private Integer decimals;
	
	@JsonCreator
	public static Currency fromCode(String code) {
		for (Currency c : Currency.values()) {
			if (c.code.equalsIgnoreCase(code)) {
				return c;
			}
		}
		return null;
	}
}
