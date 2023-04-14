package lithium.service.promo.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum Operation {
  COUNTER("counter"),
  ACCUMULATOR("accumulator"),
  LAST_VALUE("last_value");

  @Setter
  @Accessors( fluent = true )
  private String type;

  @JsonValue
  public String type() {
    return type;
  }

  @JsonCreator
  public static Operation fromType(String type) {
    for (Operation o: Operation.values()) {
      if (o.type().equalsIgnoreCase(type)) {
        return o;
      }
    }
    return null;
  }
}
