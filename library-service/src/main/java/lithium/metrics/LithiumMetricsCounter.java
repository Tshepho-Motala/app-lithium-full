package lithium.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@Slf4j
public class LithiumMetricsCounter implements NamedMetric {
    private final MeterRegistry meterRegistry;
    private final String name;
    private final List<Tag> tags;

    public void increment(){
        try {
            meterRegistry.counter(getNormalizedName(), getTags()).increment();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private List<io.micrometer.core.instrument.Tag> getTags() throws Exception{
        return Optional.ofNullable(tags)
                .orElse(Collections.emptyList())
                .stream()
                .map(Tag::toMicrometerTag)
                .toList();
    }
}
