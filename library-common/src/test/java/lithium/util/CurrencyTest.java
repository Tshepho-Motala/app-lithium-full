package lithium.util;

import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Slf4j
public class CurrencyTest {

	@Test
	public void fromAmountSixtyNinePointFourtyNineToAmount() {
		Double fromAmount = 69.49;
		BigDecimal amount = CurrencyAmount.fromAmount(fromAmount).toAmount();
		log.info("fromAmount: "+fromAmount+" toAmount: "+amount);
		assertEquals("From amount("+fromAmount+") to amount failed.", fromAmount.toString(), amount.toString());
	}
	@Test
	public void fromAmountToAmount() {
		BigDecimal fromAmount = BigDecimal.ONE;
		BigDecimal amount = CurrencyAmount.fromAmount(fromAmount).toAmount();
		log.info("fromAmount: "+fromAmount+" toAmount: "+amount);
		assertEquals("From amount("+fromAmount+") to amount failed.", fromAmount, amount);
	}
	@Test
	public void fromAmountToCents() {
		BigDecimal fromAmount = BigDecimal.ONE;
		Long amountCents = CurrencyAmount.fromAmount(fromAmount).toCents();
		log.info("fromAmount: "+fromAmount+" toCents: "+amountCents);
		assertTrue("From amount("+fromAmount+") to cents failed.", (fromAmount.compareTo(new BigDecimal(amountCents).movePointLeft(2)) == 0));
	}

	@Test
	public void fromCentsToCents() {
		Long fromCents = 100L;
		Long amountCents = CurrencyAmount.fromCents(fromCents).toCents();
		log.info("fromCents: "+fromCents+" toCents: "+amountCents);
		assertEquals("From cents("+fromCents+") to cents failed.", fromCents, amountCents);
	}
	@Test
	public void fromCentsToAmount() {
		Long fromCents = 100L;
		BigDecimal amount = CurrencyAmount.fromCents(fromCents).toAmount();
		log.info("fromCents: "+fromCents+" toAmount: "+amount);
		assertEquals("From cents("+fromCents+") to amount failed.", ((fromCents!=null)?fromCents.longValue():0L), amount.movePointRight(2).longValue());
	}

	/**
	 * Null is not 0... If we assume that, we potentially throw insufficient funds errors rather than
	 * 500 internal server errors. If balance is null from accounting, its a problem, something went wrong.
	 * We should really say something went wrong rather than defaulting.
	 **/

	@Test(expected = NullPointerException.class)
	public void fromNullCentsToAmount() {
		Long fromCents = null;
		BigDecimal amount = CurrencyAmount.fromCents(fromCents).toAmount();
		log.info("fromCents: "+fromCents+" toAmount: "+amount);
		assertEquals("From cents("+fromCents+") to amount failed.", 0L, amount.movePointRight(2).longValue());
	}

	@Test(expected = NullPointerException.class)
	public void fromNullCentsToCents() {
		Long fromCents = null;
		Long amount = CurrencyAmount.fromCents(fromCents).toCents();
		log.info("fromCents: "+fromCents+" toCents: "+amount);
		assertEquals("From cents("+fromCents+") to cents failed.", 0L, amount.longValue());
	}

	@Test(expected = NullPointerException.class)
	public void fromNullAmountToCents() {
		BigDecimal fromAmount = null;
		Long amountCents = CurrencyAmount.fromAmount(fromAmount).toCents();
		log.info("fromAmount: "+fromAmount+" toCents: "+amountCents);
		assertTrue("From amount("+fromAmount+") to cents failed.", (amountCents == 0L));
	}

	@Test
	public void fromNullAmountAllowNullToCents() {
		BigDecimal fromAmount = null;
		Long amountCents = CurrencyAmount.fromAmountAllowNull(fromAmount).toCents();
		log.info("fromAmountAllowNull: "+fromAmount+" toCents: "+amountCents);
		assertTrue("From amount("+fromAmount+") to cents failed.", (fromAmount == null && amountCents == null));
	}
	@Test
	public void fromNullDoubleAmountAllowNullToCents() {
		Double fromAmount = null;
		Long amountCents = CurrencyAmount.fromAmountAllowNull(fromAmount).toCents();
		log.info("fromAmountAllowNull: "+fromAmount+" toCents: "+amountCents);
		assertTrue("From amount("+fromAmount+") to cents failed.", (fromAmount == null && amountCents == null));
	}
	@Test
	public void fromNullIntegerAmountAllowNullToCents() {
		Integer fromAmount = null;
		Long amountCents = CurrencyAmount.fromAmountAllowNull(fromAmount).toCents();
		log.info("fromAmountAllowNull: "+fromAmount+" toCents: "+amountCents);
		assertTrue("From amount("+fromAmount+") to cents failed.", (fromAmount == null && amountCents == null));
	}
	@Test
	public void fromNullCentsAllowNullToAmount() {
		Long fromCents = null;
		BigDecimal amount = CurrencyAmount.fromCentsAllowNull(fromCents).toAmount();
		log.info("fromCentsAllowNull: "+fromCents+" toAmount: "+amount);
		assertTrue("From amount("+fromCents+") to cents failed.", (fromCents == null && amount == null));
	}

	@Test(expected = NullPointerException.class)
	public void fromNullAmountToAmount() {
		BigDecimal fromAmount = null;
		BigDecimal amount = CurrencyAmount.fromAmount(fromAmount).toAmount();
		log.info("fromAmount: "+fromAmount+" toAmount: "+amount);
		assertTrue("From amount("+fromAmount+") to amount failed.", (amount.compareTo(BigDecimal.ZERO) == 0));
	}

	@Test
	public void fromAmountStringToAmount() {
		String fromAmountString = "1111.11";
		BigDecimal amount = CurrencyAmount.fromAmountString(fromAmountString).toAmount();
		log.info("fromAmount: "+fromAmountString+" toAmount: "+amount);
		assertTrue("From amount("+fromAmountString+") to amount failed.", (amount.toPlainString().contentEquals(fromAmountString)));
	}

	@Test(expected = NumberFormatException.class)
	public void fromAmountStringToNumberFormatException() {
		String fromAmountString = "111.23qwe";
		BigDecimal amount = CurrencyAmount.fromAmountString(fromAmountString).toAmount();
		fail();
	}

	@Test
	public void fromCentsStringToCents() {
		String fromCentsString = "111111";
		Long cents = CurrencyAmount.fromCentsString(fromCentsString).toCents();
		log.info("fromAmount: "+fromCentsString+" toAmount: "+cents);
		assertTrue("From amount("+fromCentsString+") to amount failed.", (cents == 111111L));
	}

	@Test(expected = ArithmeticException.class)
	public void fromCentsStringToArithmeticException() {
		String fromCentsString = "111111.4";
		Long cents = CurrencyAmount.fromCentsString(fromCentsString).toCents();
		fail();
	}

	@Test(expected = NumberFormatException.class)
	public void fromCentsStringToNumberFormatException() {
		String fromCentsString = "111111.rr";
		Long cents = CurrencyAmount.fromCentsString(fromCentsString).toCents();
		fail();
	}

}