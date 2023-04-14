package lithium.service.user.provider.threshold.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum Type {

  LIMIT_TYPE_WIN(1, "LIMIT_TYPE_WIN", "Limit type win"),
  LIMIT_TYPE_LOSS(2, "LIMIT_TYPE_LOSS", "Limit type loss");

  @Getter
  @Accessors(fluent=true)
  private int id;

  @Getter
  @Accessors(fluent = true)
  private String typeName;

  @Getter
  @Accessors(fluent = true)
  private String displayName;

  @JsonCreator
  public static Type fromName(String name) {
    for (Type s: Type.values()) {
      if (s.typeName.equalsIgnoreCase(name)) {
        return s;
      }
    }
    return null;
  }

  @JsonCreator
  public static Type fromId(int id) {
    for (Type s: Type.values()) {
      if (s.id == id) {
        return s;
      }
    }
    return null;
  }
}


