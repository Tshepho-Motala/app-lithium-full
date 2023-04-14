package lithium.csv.provider.user.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GenerationRecordType {
    LOGIN_EVENTS("login-events");

    @Getter
    @Accessors(fluent = true)
    private final String type;

    public static GenerationRecordType fromType(String type) {
        return Arrays.stream(GenerationRecordType.values()).filter(t -> t.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
