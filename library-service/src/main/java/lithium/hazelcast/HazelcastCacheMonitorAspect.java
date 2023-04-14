package lithium.hazelcast;

import lithium.metrics.LithiumMetricsService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class HazelcastCacheMonitorAspect {
	@Autowired private LithiumMetricsService metrics;

	@Value("${lithium.hazelcast-client.cache.monitor.thresholds.info-ms:500}")
	private long infoThresholdMillis;
	@Value("${lithium.hazelcast-client.cache.monitor.thresholds.warning-ms:1500}")
	private long warningThresholdMillis;
	@Value("${lithium.hazelcast-client.cache.monitor.thresholds.error-ms:2000}")
	private long errorThresholdMillis;

	@Pointcut("execution(* com.hazelcast.spring.cache.HazelcastCacheManager.getCache(..))")
	public void getCache() {};

	@Around("getCache()")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		String cacheName = Arrays.stream(joinPoint.getArgs())
				.map(Object::toString)
				.collect(Collectors.joining(", "));
		return metrics.timer(null, infoThresholdMillis, warningThresholdMillis, errorThresholdMillis)
				.timeViaAspect("cacheget." + cacheName, sw -> {
					return joinPoint.proceed();
				});
	}
}
