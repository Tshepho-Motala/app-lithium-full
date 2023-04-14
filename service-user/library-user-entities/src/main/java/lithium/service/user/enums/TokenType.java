package lithium.service.user.enums;

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
public enum TokenType {
  NUMERIC("n"),
  ALPHANUMERIC("an");

  @Getter
  @Setter
  @Accessors(fluent = true)
  private String type;

  @JsonCreator
  public static TokenType fromType(String tokenType) {
    for (TokenType tt : TokenType.values()) {
      if (tt.type.equalsIgnoreCase(tokenType)) {
        return tt;
      }
    }
    return null;
  }
}
