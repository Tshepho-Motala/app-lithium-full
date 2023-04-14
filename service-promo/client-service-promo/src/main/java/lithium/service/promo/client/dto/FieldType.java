package lithium.service.promo.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum FieldType {
    TYPE_INPUT("input"),
    TYPE_MULTISELECT("multiselect"),
    TYPE_SINGLESELECT("singleselect");

    @Getter
    @Setter
    @Accessors(fluent = true)
    @JsonValue
    private String type;

}
