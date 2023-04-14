package lithium.math;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import lithium.util.CurrencyJsonSerializer;

/**
 * Convenience class to convert between cents (long) and amount (BigDecimal) without needing to check for null values.
 * If null is passed in 0 will be returned.
 * <p>Example Usage: </p>
 * <blockquote><pre>
 *      BigDecimal amount = BigDecimal.ONE;
 *      Long amountCents = 100L;
 *
 *      Long amountCents = CurrencyAmount.fromAmount(amount).toCents(); // Converts amount: 1 to amountCents: 100
 *      Long amountCents = CurrencyAmount.fromCents(amountCents).toCents(); // Converts amountCents: 100 to 100
 *      BigDecimal amount = CurrencyAmount.fromAmount(amount).toAmount(); // Converts amount: 1 to amount: 1
 *      BigDecimal amount = CurrencyAmount.fromCents(amountCents).toAmount();  // Converts amountCents: 100 to amount: 1.00
 *
 * </pre></blockquote>
 * @author Riaan
 */

@Slf4j
@JsonSerialize(using = CurrencyJsonSerializer.class)
public class CurrencyAmount {
	private BigDecimal amount;
	private boolean returnNull = false;

	private CurrencyAmount() {
		returnNull = true;
	}

	private CurrencyAmount(BigDecimal amount) {
		this.amount = amount;
	}

	private CurrencyAmount(Double amount) {
		this.amount = BigDecimal.valueOf(amount);
	}

	private CurrencyAmount(Integer amount) {
		this.amount = BigDecimal.valueOf(amount);
	}

	public static CurrencyAmount fromAmount(BigDecimal amount) {
		return new CurrencyAmount(amount);
	}
	public static CurrencyAmount fromAmountAllowNull(BigDecimal amount) {
		if (amount == null) return new CurrencyAmount();
		return new CurrencyAmount(amount);
	}

	public static CurrencyAmount fromAmount(Double amount) {
		return new CurrencyAmount(amount);
	}
	public static CurrencyAmount fromAmountAllowNull(Double amount) {
		if (amount == null) return new CurrencyAmount();
		return new CurrencyAmount(amount);
	}

	public static CurrencyAmount fromAmount(Integer amount) {
		return new CurrencyAmount(amount);
	}
	public static CurrencyAmount fromAmountAllowNull(Integer amount) {
		if (amount == null) return new CurrencyAmount();
		return new CurrencyAmount(amount);
	}

	public static CurrencyAmount fromCents(Long amountCents) {
		return new CurrencyAmount(BigDecimal.valueOf(amountCents / 100.0));
	}
	public static CurrencyAmount fromCentsAllowNull(Long amountCents) {
		if (amountCents == null) return new CurrencyAmount();
		return new CurrencyAmount(BigDecimal.valueOf(amountCents / 100.0));
	}

	public static CurrencyAmount fromAmountString(String amount) throws NumberFormatException {
		BigDecimal amountBd = new BigDecimal(amount);
		return fromAmount(amountBd);
	}

	public static CurrencyAmount fromCentsString(String amountCents) throws NumberFormatException, ArithmeticException {
		BigDecimal amountBd = new BigDecimal(amountCents);
		return fromCents(amountBd.longValueExact());
	}

	public static String formatUsingLocale(Long amountCents, Locale locale, String currencySymbol, String currencyCode)  throws NumberFormatException{
		Currency currency = Currency.getInstance(currencyCode);
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		format.setCurrency(currency);
		DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
		decimalFormatSymbols.setCurrencySymbol(currencySymbol);
		((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);
		return format.format(CurrencyAmount.fromCents(amountCents).toAmount()).trim();
	}


	public Long toCents() {
		if (returnNull) return null;
		return amount.movePointRight(2).longValue();
	}

	public BigDecimal toAmount() {
		return amount;
	}
	public BigDecimal toAmountReverse() {
		return amount.negate();
	}

	public boolean isNegative() {
		return (BigDecimal.ZERO.compareTo(amount) > 0);
	}

	public void add(BigDecimal amount) {
		this.amount = this.amount.add(amount);
	}

	public void subtract(BigDecimal amount) {
		this.amount = this.amount.subtract(amount);
	}

	public void add(CurrencyAmount amount) {
		this.add(amount.toAmount());
	}

	public void subtract(CurrencyAmount amount) {
		this.subtract(amount.toAmount());
	}

	public void addCents(long cents) {
		this.add(CurrencyAmount.fromCents(cents));
	}

	public void subtractCents(long cents) {
		this.subtract(CurrencyAmount.fromCents(cents));
	}

}