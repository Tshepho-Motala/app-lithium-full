package lithium.service.kyc.entities;

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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum KYCDocumentType {
    PHOTO_BASE64(0, "PHOTO_BASE64");
    @Setter
    @Accessors(fluent = true)
    private Integer id;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private String field;
    @JsonCreator
    public static KYCDocumentType fromId(Integer id) {
        for (KYCDocumentType o : KYCDocumentType.values()) {
            if (o.id.compareTo(id) == 0) {
                return o;
            }
        }
        return null;
    }

    @JsonValue
    public Integer id() {
        return id;
    }
}
