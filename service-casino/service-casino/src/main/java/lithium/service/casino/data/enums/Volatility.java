package lithium.service.casino.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This was created to replace the constant variables in lithium.service.casino.data.entities.BonusRevision BonusType should not be confused with the
 * transaction types that could be associated with a bonus. In the current model, a 'freespin' bonus could have a cash and a freespin component linked
 * to it, meaning it will end up having two different transaction types when the bonus is allocated to a player. E.g. a Signup bonus can award
 * freespins and cash, meaning it will have transaction types for both connected to it.
 */
@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum Volatility {
  LOW(1, "VOLATILITY_LOW", "LOW"),
  MEDIUM(2, "VOLATILITY_MEDIUM", "MEDIUM"),
  HIGH(3, "VOLATILITY_HIGH", "HIGH"), //Virtual Coins
  FIXED(4, "VOLATILITY_FIXED", "FIXED");

  @Setter
  @Accessors( fluent = true )
  private Integer id;
  @Getter
  @Setter
  @Accessors( fluent = true )
  private String type;
  @Getter
  @Setter
  @Accessors( fluent = true )
  private String shortName;


  @JsonValue
  public Integer id () {
    return id;
  }

//    @JsonCreator
//    public static Volatility fromId (int id) {
//      for (Volatility v: Volatility.values()) {
//        if (v.id == id) {
//          return v;
//        }
//      }
//      return null;
//    }

//    @JsonCreator
//    public static Volatility fromType (String type) {
//      for (Volatility v: Volatility.values()) {
//        if (v.type.equalsIgnoreCase(type)) {
//          return v;
//        }
//      }
//      return null;
//    }

@JsonCreator
public static Volatility fromShortName (String shortName) {
  for (Volatility v: Volatility.values()) {
    if (v.shortName.equalsIgnoreCase(shortName)) {
      return v;
    }
  }
  return null;
}
}
