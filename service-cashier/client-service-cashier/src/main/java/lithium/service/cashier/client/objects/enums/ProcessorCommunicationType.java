package lithium.service.cashier.client.objects.enums;

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
public enum ProcessorCommunicationType {
  MAIL(0, "MAIL"),
  NOTIFICATION(1, "NOTIFICATION"),
  ALL(2, "ALL"),
  SMS(3, "SMS");

  @Setter
  @Accessors(fluent = true)
  private Integer id;

  @Getter
  @Setter
  @Accessors(fluent = true)
  private String type;

  @JsonCreator
  public static ProcessorCommunicationType fromType(String type) {
    for (ProcessorCommunicationType t : ProcessorCommunicationType.values()) {
      if (t.type.equalsIgnoreCase(type)) {
        return t;
      }
    }
    return null;
  }

  @JsonCreator
  public static ProcessorCommunicationType fromId(Integer id) {
    for (ProcessorCommunicationType t : ProcessorCommunicationType.values()) {
      if (t.id.compareTo(id) == 0) {
        return t;
      }
    }
    return null;
  }

  @JsonValue
  public Integer id() {
    return id;
  }
}
