package lithium.metrics;

import javax.annotation.PostConstruct;

import io.micrometer.core.instrument.MeterRegistry;
import lithium.metrics.builders.Metric;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LithiumMetricsService {

	MeterRegistry meterRegistry;
	LithiumMetricsConfigurationProperties config;
	
	@PostConstruct
	public void init() {
		log.info("Lithium metrics services created. " + config);
	}
	
	public LithiumMetricsTimer timer(Logger log) {
		return LithiumMetricsTimer.builder()
				.meterRegistry(meterRegistry)
				.logger(log)
				.errorThreshold(config.getErrorThresholdMillis())
				.warnThreshold(config.getWarningThresholdMillis())
				.infoThreshold(config.getInfoThresholdMillis())
				.build();
	}
	
	public LithiumMetricsTimer timer(Logger log,Long infoMsThresh) {
		return LithiumMetricsTimer.builder()
				.meterRegistry(meterRegistry)
				.logger(log)
				.errorThreshold(config.getErrorThresholdMillis())
				.warnThreshold(config.getWarningThresholdMillis())
				.infoThreshold(infoMsThresh)
				.build();
	}

	/**
	 * Use this with care, if the thresholds are too high nobody will ever know things are broken
	 * @param log
	 * @param infoMsThresh
	 * @param warnMsThresh
	 * @param errMsThresh
	 * @return
	 */
	public LithiumMetricsTimer timer(Logger log,Long infoMsThresh, Long warnMsThresh, Long errMsThresh) {
		return LithiumMetricsTimer.builder()
				.meterRegistry(meterRegistry)
				.logger(log)
				.errorThreshold(errMsThresh)
				.warnThreshold(warnMsThresh)
				.infoThreshold(infoMsThresh)
				.build();
	}

	public void increment(Metric metric) {
		try {
			meterRegistry.counter(metric.build()).increment();
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

	//TODO: fix usage and remove method, with Prometheus counters should only increase monotonically
	public void decrement(Metric metric) {
		try {
			meterRegistry.counter(metric.build()).increment(-1.0);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

	public LithiumMetricsCounter counter(@NonNull String name){
		return counter(name, null);
	}

	public LithiumMetricsCounter counter(@NonNull String name, @Nullable List<Tag> tags){
		return LithiumMetricsCounter.builder()
				.meterRegistry(meterRegistry)
				.name(name)
				.tags(tags)
				.build();
	}
}
