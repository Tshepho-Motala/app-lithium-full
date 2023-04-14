package lithium.service.reward.provider.casino.roxor;

import lithium.service.reward.client.dto.IRewardTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.stream.Stream;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTypeName implements IRewardTypeName {
  FREESPIN("freespin", "free_spin"),
  INSTANT_REWARD("instant_reward", "instant_reward"),
  INSTANT_REWARD_FREESPIN("instant_reward_freespin", "instant_reward"),
  // casino_chips == casino freebet - [2022/06/07 16:51] Alex Bamford: maybe we need to rename it all over the place to casino chips
  // as that seems to be the new name everyone has adapted to working with lately
  CASINO_CHIP("casino_chip", "bonus_cash");

  @Getter
  @Accessors(fluent = true)
  private String rewardTypeName;

  @Getter
  @Accessors(fluent = true)
  private String rgpName;

  public static RewardTypeName fromName(String name) {
    return Stream.of(values())
            .filter(v -> v.rewardTypeName.equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
  }
}