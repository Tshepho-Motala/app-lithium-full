package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum AlternativeMessageAction {
    INCREMENT,
    DECREMENT;

    public static AlternativeMessageAction fromName(String name) {
        return Arrays.stream(values()).filter(v-> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
