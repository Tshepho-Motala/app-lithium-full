package lithium.service.document.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum DocumentPurpose {
    INTERNAL(0, "Internal"),
    EXTERNAL(1, "External");

    @Setter
    @Accessors(fluent=true)
    private Integer id;

    @Getter
    @Setter
    @Accessors(fluent=true)
    private String purpose;

    @JsonValue
    public Integer id() {
        return id;
    }

    public static DocumentPurpose fromPurpose(String purpose) {
        for (DocumentPurpose o: DocumentPurpose.values()) {
            if (o.purpose.equalsIgnoreCase(purpose)) {
                return o;
            }
        }
        return null;
    }

    @JsonCreator
    public static DocumentPurpose fromId(Integer id) {
        for (DocumentPurpose o: DocumentPurpose.values()) {
            if (o.id.compareTo(id) == 0) {
                return o;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
