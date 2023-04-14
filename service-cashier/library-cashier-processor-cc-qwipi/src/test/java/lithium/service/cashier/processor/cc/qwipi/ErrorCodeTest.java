package lithium.service.cashier.processor.cc.qwipi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;

public class ErrorCodeTest {

	@Test
	public void testErrorCodeIntegerString() {
		assertFalse("Default for success should be false", ErrorCode.E1000010.success());
		assertTrue("Default for softFail should be false", ErrorCode.E1000010.softFail());
		
	}

	@Test
	public void testErrorCodeIntegerStringBoolean() {
		assertTrue("Success should be true when passed as argument on constructor", ErrorCode.E0000000.success());
		assertTrue("Default for softFail should be false", ErrorCode.E1000010.softFail());
	}

	@Test
	public void testErrorCodeIntegerStringBooleanBoolean() {
		assertFalse("Explicit success should be false", ErrorCode.E1000470.success());
		assertFalse("Explicit softFail should be false", ErrorCode.E1000470.softFail());
	}

}
