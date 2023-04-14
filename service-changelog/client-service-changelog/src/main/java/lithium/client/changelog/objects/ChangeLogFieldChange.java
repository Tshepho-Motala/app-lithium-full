package lithium.client.changelog.objects;

import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.LocaleUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeLogFieldChange {
	private long id;
	private String field;
	private String fromValue;
	private String toValue;
	private String editedBy;

	private static final List<String> currencyTypeFields = Arrays.asList(new String[]{"amount"});
	public static void formatCurrencyFields(List<ChangeLogFieldChange> clfc, String localeStr, String currencySymbol, String currencyCode) {
		clfc.stream()
				.filter(changeLogFieldChange -> currencyTypeFields.contains(changeLogFieldChange.getField()))
				.forEach(changeLogFieldChange -> changeLogFieldChange.formatCurrencyValues(localeStr, currencySymbol, currencyCode));
	}

	public static String getToAmountValue(List<ChangeLogFieldChange> clfc) {
		return clfc.stream()
				.filter(changeLogFieldChange -> currencyTypeFields.contains(changeLogFieldChange.getField()))
				.findFirst()
				.map(ChangeLogFieldChange::getToValue)
				.orElse(null);
	}

	public static String getFromAmountValue(List<ChangeLogFieldChange> clfc) {
		return clfc.stream()
				.filter(changeLogFieldChange -> currencyTypeFields.contains(changeLogFieldChange.getField()))
				.findFirst()
				.map(ChangeLogFieldChange::getFromValue)
				.orElse(null);
	}

	private void formatCurrencyValues(String localeStr, String currencySymbol, String currencyCode) {
		try {
			localeStr = localeStr.replace("-", "_");
			Locale locale = LocaleUtils.toLocale(localeStr);

			if (fromValue == null || fromValue.isEmpty()) this.fromValue = "0";
			if (toValue == null || toValue.isEmpty()) this.toValue = "0";

			String strFromValue = fromValue.contains(".") ? new BigDecimal(fromValue).movePointRight(2) .toString() : fromValue;
			String strToValue = toValue.contains(".") ? new BigDecimal(toValue).movePointRight(2).toString() : toValue;

			this.fromValue = CurrencyAmount.formatUsingLocale(Long.valueOf(strFromValue), locale, currencySymbol, currencyCode);
			this.toValue = CurrencyAmount.formatUsingLocale(Long.valueOf(strToValue), locale, currencySymbol, currencyCode);
		} catch (Exception e) {
			log.error("Error thrown while trying to change ChangeLogField values to currency with exception {}", e);
		}
	}
}

