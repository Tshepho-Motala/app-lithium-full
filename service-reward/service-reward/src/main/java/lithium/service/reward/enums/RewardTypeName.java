package lithium.service.reward.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lithium.service.reward.client.dto.IRewardTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTypeName implements IRewardTypeName {
  CASH("cash"),
  UNLOCK_GAMES("unlock_games");

  @Getter
  @Accessors(fluent = true)
  private String rewardTypeName;

  @JsonCreator
  public static RewardTypeName fromType(String type) {
    return Arrays.stream(RewardTypeName.values()).filter(rt -> rt.rewardTypeName().equalsIgnoreCase(type))
            .findFirst().orElse(null);
  }
}