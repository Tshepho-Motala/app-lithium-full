package lithium.metrics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * An aspect around methods marked with {@link TimeThisMethod} that utilises the existing
 * Lithium timer pattern. Simplifies metrics logging.
 * @see TimeThisMethod
 */
@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class LithiumMetricsTimerAspect {

    LithiumMetricsConfigurationProperties config;
    LithiumMetricsService service;

    @Around("@annotation(timeThisMethod)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, TimeThisMethod timeThisMethod) throws Throwable {

        String metricName = joinPoint.getTarget().getClass().getName().toLowerCase();
        metricName += "." + joinPoint.getSignature().getName();
//        metricName = metricName.replace('.', '_');

        long infoThresholdMillis = (timeThisMethod.infoThresholdMillis() > 0)
                ? timeThisMethod.infoThresholdMillis()
                : config.getInfoThresholdMillis();
        long warningThresholdMillis = (timeThisMethod.warningThresholdMillis() > 0)
                ? timeThisMethod.warningThresholdMillis()
                : config.getWarningThresholdMillis();
        long errorThresholdMillis = (timeThisMethod.errorThresholdMillis() > 0)
                ? timeThisMethod.errorThresholdMillis()
                : config.getErrorThresholdMillis();

        return service.timer(null, infoThresholdMillis, warningThresholdMillis, errorThresholdMillis)
                .timeViaAspect(metricName, sw -> joinPoint.proceed());
    }
}