package lithium.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class LithiumMetricsTimer {

	MeterRegistry meterRegistry;
	private Logger logger;
	private long errorThreshold;
	private long warnThreshold;
	private long infoThreshold;

	public <T> T time(CallableWithStopWatch<T> callable) throws Exception {
		return time(null, callable);
	}

	public void time(RunnableWithStopWatch runnable) throws Exception {
		time(null, runnable);
	}

	public <T> T time(String name, CallableWithStopWatch<T> callable) throws Exception {
		try {
			return time(name, callable, null);
		} catch (Throwable ex) {
			throw new Exception(ExceptionMessageUtil.allMessages(ex), ex);
		}
	}

	public void time(String name, RunnableWithStopWatch runnable) throws Exception {
		try {
			time(name, null, runnable);
		} catch (Throwable ex) {
			throw new Exception(ExceptionMessageUtil.allMessages(ex), ex);
		}
	}

	public <T> T timeViaAspect(String name, CallableWithStopWatch<T> callable) throws Throwable {
		return time(name, callable, null);
	}

	private <T> T time(String name, CallableWithStopWatch<T> callable, RunnableWithStopWatch runnable) throws Throwable {
		String metricName = "";
		if (logger != null) metricName = logger.getName().toLowerCase().replace(".", "_");
		if (metricName.length() > 0) metricName += ".";
		metricName += name;

		StopWatch swinternal = new StopWatch(metricName, SW.getFromThreadLocalAllowNull());
		Exception exception = null;
		T result = null;

		SW.storeInThread(swinternal);
		Timer timer = Timer.builder("timer." + metricName)
				.publishPercentiles(0.999)
				.register(meterRegistry);
		Timer.Sample sample = Timer.start(meterRegistry);
		long totalTimeMillis;
		try {
			if (runnable != null) 
				runnable.run(swinternal);
			if (callable != null)
				result = callable.call(swinternal);
		} catch (Exception e) {
			exception = e;
		} finally {
			totalTimeMillis = TimeUnit.NANOSECONDS.toMillis(sample.stop(timer));
			SW.removeFromThread();
		}

		if (exception != null) {
			meterRegistry.counter("meter." + metricName + ".error").increment();
			throw exception;
		}
		
		String printout = metricName;
		if (swinternal.getTaskCount() > 0) {
			printout = swinternal.prettyPrint();
		}

		if (logger == null) logger = log;

		if (totalTimeMillis >= errorThreshold) {
			logger.error("Took longer than " + errorThreshold + "ms: " + totalTimeMillis + " " + printout);
		} else if (totalTimeMillis >= warnThreshold) {
			logger.warn("Took longer than " + warnThreshold + "ms: " + totalTimeMillis + " " + printout);
		} else if (totalTimeMillis >= infoThreshold) {
			logger.info("Took longer than " + infoThreshold + "ms: " + totalTimeMillis + " " + printout);
		} else {
			logger.debug(printout);
		}
		return result;
	}
}
