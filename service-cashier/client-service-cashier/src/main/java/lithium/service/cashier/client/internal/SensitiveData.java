package lithium.service.cashier.client.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

@AllArgsConstructor
public enum SensitiveData {
    CVV ("cvv", "[0-9]{3,4}", "***");

    @Getter
    private String name;

    @Getter
    private String regex;

    @Getter
    private String mask;

    public String applyMask(String value) {
        return Optional.ofNullable(value).map(v -> Pattern.compile(this.regex).matcher(v).replaceAll(this.mask)).orElse(null);
    }

    public static String maskSensitive(String name, String value) {
        return Optional.ofNullable(value)
                .map( v -> isSensitive(name)
                        .map(s -> s.applyMask(v))
                        .orElse(v))
                .orElse(null);
    }
    public static Optional<SensitiveData> isSensitive(String name) {
        return Optional.ofNullable(name)
                .map(n -> Arrays.stream(SensitiveData.values())
                        .filter(s -> s.getName().equalsIgnoreCase(n))
                        .findFirst())
                .orElse(Optional.empty());
    }
}
