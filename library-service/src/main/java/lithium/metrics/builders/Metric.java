package lithium.metrics.builders;

import java.util.function.Function;

public interface Metric {
    default String format(String s) {
        return s.replaceAll("[^a-zA-Z0-9-_]", "");
    }
    String build() throws Exception;
}
