package lithium.service.document.generation.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocumentGenerationStatus {
    CREATED(0),
    BUSY(1),
    COMPLETE(2),
    FAILED(3),
    DOWNLOADED(4),
    CANCELED(5);

    @Getter
    private int value;

    @JsonCreator
    public static DocumentGenerationStatus fromValue(int value) {
        for (DocumentGenerationStatus s : DocumentGenerationStatus.values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("Can't resolve DocumentGenerationStatus from value: " + value);
    }

    public static DocumentGenerationStatus fromName(String name) {
        for (DocumentGenerationStatus s : DocumentGenerationStatus.values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Can't resolve DocumentGenerationStatus from name: " + name);
    }
}
