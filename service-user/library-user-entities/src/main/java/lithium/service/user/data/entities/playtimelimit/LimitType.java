package lithium.service.user.data.entities.playtimelimit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LimitType {

  TYPE_PLAY_TIME_LIMIT_ACTIVE(1),
  TYPE_PLAY_TIME_LIMIT_PENDING(2);
  
  @Setter
  @Accessors(fluent = true)
  private Integer type;

  @JsonValue
  public Integer type() {
    return type;
  }

  @JsonCreator
  public static LimitType fromType(int type) {
    for (LimitType lt : LimitType.values()) {
      if (lt.type == type) {
        return lt;
      }
    }
    return null;
  }
}
