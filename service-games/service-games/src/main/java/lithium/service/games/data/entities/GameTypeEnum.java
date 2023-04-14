package lithium.service.games.data.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GameTypeEnum {

    PRIMARY("primary"),
    SECONDARY("secondary");

    @Getter
    @Setter
    @Accessors(fluent = true)
    private String value;

    public String getValue() {
      return value;
    }

    @JsonCreator
    public static GameTypeEnum fromType(String type) {
      for (GameTypeEnum g : GameTypeEnum.values()) {
        if (g.value.equalsIgnoreCase(type)) {
          return g;
        }
      }
      return null;
    }

}
