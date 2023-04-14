package lithium.service.user.threshold.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lithium.service.limit.client.LimitType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum EType {
  TYPE_WIN_LIMIT(LimitType.TYPE_WIN_LIMIT.name(), LimitType.TYPE_WIN_LIMIT.type()),
  TYPE_LOSS_LIMIT(LimitType.TYPE_LOSS_LIMIT.name(), LimitType.TYPE_LOSS_LIMIT.type()),
  TYPE_DEPOSIT_LIMIT(LimitType.TYPE_DEPOSIT_LIMIT.name(), LimitType.TYPE_DEPOSIT_LIMIT.type());

  @Setter
  @Accessors( fluent = true )
  private String name;

  @Setter
  @Getter
  @Accessors( fluent = true )
  private Integer type;

  @JsonCreator
  public static EType fromName(String typeName) {
    for (EType type: EType.values()) {
      if (type.name.equalsIgnoreCase(typeName)) {
        return type;
      }
    }
    return null;
  }

  public static EType fromType(Integer type) {
    for (EType t: EType.values()) {
      if (t.type == type) {
        return t;
      }
    }
    return null;
  }
}


