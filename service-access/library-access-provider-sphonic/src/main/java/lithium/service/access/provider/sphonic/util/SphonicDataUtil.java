package lithium.service.access.provider.sphonic.util;

import java.util.Optional;
import java.util.function.Supplier;

public class SphonicDataUtil {
	public static <T> Optional<T> resolve(Supplier<T> resolver) {
		try {
			T result = resolver.get();
			return Optional.ofNullable(result);
		} catch (NullPointerException e) {
			return Optional.empty();
		}
	}
}
