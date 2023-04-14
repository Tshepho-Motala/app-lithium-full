package lithium.service.cashier.processor.cc.trustspay.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class HashCalculatorTest {

	@Test
	public void testLengthShouldBe64() {
		HashCalculator calculator = new HashCalculator("password");
		for (int i=0; i<10; i++) {
			calculator.addItem("Test" + i);
			String hash = calculator.calculateHash();
			assertEquals("Length of hash should be 64", 64, hash.length());
		}
	}
	
	@Test
	public void testValueShouldBeFixed() {
		HashCalculator calculator = new HashCalculator("password");
		calculator.addItem("Test");
		String hash = calculator.calculateHash();
		assertEquals("Value of hash should be 67E42AED28AA3B17114C6B9C97482B90F54E567C8172BCA869A0D64BCFE6EFDB", 
				"67E42AED28AA3B17114C6B9C97482B90F54E567C8172BCA869A0D64BCFE6EFDB", hash);
	}

	@Test
	public void testHashShouldFail() {
		HashCalculator calculator = new HashCalculator("newpassword");
		calculator.addItem("Test");
		String hash = calculator.calculateHash();
		assertNotEquals("Value of hash should not be 67E42AED28AA3B17114C6B9C97482B90F54E567C8172BCA869A0D64BCFE6EFDB", 
				"67E42AED28AA3B17114C6B9C97482B90F54E567C8172BCA869A0D64BCFE6EFDB", hash);
	}


}
