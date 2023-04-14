package lithium.metrics;

import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

public interface NamedMetric {

    Pattern notAllowedCharacters = Pattern.compile("[^a-zA-Z0-9-_]");

    static String normalize(String name) {
        return notAllowedCharacters.matcher(name).replaceAll("");
    }

    String getName();

    default String getNormalizedName() {
        return ofNullable(getName())
                .map(NamedMetric::normalize)
                .filter(not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Name can't be null or empty"))
                .toLowerCase();
    }
}
