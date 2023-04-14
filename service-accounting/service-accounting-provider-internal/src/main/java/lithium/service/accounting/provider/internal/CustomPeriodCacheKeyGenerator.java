package lithium.service.accounting.provider.internal;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

@Slf4j
public class CustomPeriodCacheKeyGenerator implements KeyGenerator {
	@Override
	public Object generate(Object target, Method method, Object... params) {
		DateTime date = new DateTime();
		StringBuilder keyBuilder = new StringBuilder(
			target.getClass().getSimpleName() + "_" +
			method.getName() + "_"
		);
		for (Object p:params) {
			if (p instanceof Integer) {
				keyBuilder.append(p).append("_");
			}
			if (p instanceof Domain) {
				keyBuilder.append(((Domain) p).getName()).append("_");
			}
			if (p instanceof DateTime) {
				date = (DateTime)p;
			}
		}
		keyBuilder.append(
			date.getYear() + "_" +
			date.getMonthOfYear() + "_" +
			date.getDayOfMonth() + "_" +
			date.getWeekOfWeekyear() + "_"
		);
		String key = keyBuilder.toString();
		log.trace("CustomPeriodCacheKeyGenerator: "+key);
		return key;
	}
}
