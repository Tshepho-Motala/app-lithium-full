package lithium.service.cashier.processor.flexepin.util;

import java.util.concurrent.ThreadLocalRandom;

public class NonceGenerator {
	public static String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		ThreadLocalRandom.current().ints(0, 100).distinct().limit(5).forEach(randomNumber -> {
			sb.append(randomNumber);
		});
		return sb.toString();
	}
}
