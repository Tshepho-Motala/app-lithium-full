package lithium.service.promo.client.dto;

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
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum FieldDataType {
  TYPE_STRING("string"),
  TYPE_NUMBER("number"),
  TYPE_BOOLEAN("boolean"),
  TYPE_MONEY("money"),
  TYPE_CURRENCY("currency");

  @Getter
  @Setter
  @Accessors(fluent = true)
  @JsonValue
  private String type;

  @JsonCreator
  public static FieldDataType fromType(String type) {
    for (FieldDataType g: FieldDataType.values()) {
      if (g.type.equalsIgnoreCase(type)) {
        return g;
      }
    }
    return null;
  }
}