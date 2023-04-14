package lithium.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * An aspect around methods marked with {@link TimeThisMethod} that utilises the existing
 * Lithium timer pattern. Simplifies metrics logging.
 *
 * @see TimeThisMethod
 */
@Aspect
@AllArgsConstructor
@NoArgsConstructor
@Component
@Slf4j
public class LithiumMetricsCounterAspect {

    MeterRegistry meterRegistry;
    private Logger logger;

    @Around("@annotation(CountThisMethod)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getTarget().getClass().getName().toLowerCase();
        metricName += "." + joinPoint.getSignature().getName();
        meterRegistry.counter(metricName).increment();
        if (logger == null) logger = log;
        logger.debug("meter." + metricName);
        return joinPoint.proceed();
    }
}