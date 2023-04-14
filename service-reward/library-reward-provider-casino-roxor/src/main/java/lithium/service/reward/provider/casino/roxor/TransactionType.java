package lithium.service.reward.provider.casino.roxor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum TransactionType { //extends CasinoTranType { cannot extend enum
  PLAYER_BALANCE ("PLAYER_BALANCE", false);

  @Setter
  @Getter
  @Accessors(fluent = true)
  private String value;

  @Setter
  @Getter
  @Accessors(fluent = true)
  private boolean bet;

  public String toString() {
    return value;
  }
}
