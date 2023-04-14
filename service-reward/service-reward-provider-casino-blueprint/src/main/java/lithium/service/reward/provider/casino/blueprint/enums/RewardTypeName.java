package lithium.service.reward.provider.casino.blueprint.enums;

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
  FREESPIN("freespin");

  @Getter
  @Accessors(fluent = true)
  private String rewardTypeName;

  @JsonCreator
  public static RewardTypeName fromName(String name) {
    return Arrays.stream(values()).filter(v -> v.rewardTypeName.equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
  }

}