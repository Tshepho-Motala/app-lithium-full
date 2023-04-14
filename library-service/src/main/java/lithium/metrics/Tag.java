package lithium.metrics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tag implements NamedMetric {

    private final String key;
    private final String value;

    @Override
    public String getName() {
        return key;
    }

    public io.micrometer.core.instrument.Tag toMicrometerTag() throws IllegalArgumentException {
        return io.micrometer.core.instrument.Tag.of(getNormalizedName(), value);
    }

    public static Tag of(String key, String value) {
        return new Tag(key, value);
    }
}
