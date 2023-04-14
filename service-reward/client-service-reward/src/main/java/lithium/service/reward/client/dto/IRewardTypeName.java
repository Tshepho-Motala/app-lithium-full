package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 * This should be implemented by all RewardTypeName enums defined in reward providers
 */
public interface IRewardTypeName extends Serializable {

  @JsonValue
  @JsonProperty( "rewardTypeName" )
  String rewardTypeName();

  @JsonCreator
  static IRewardTypeName fromRewardTypeName(IRewardTypeName[] values, String rewardTypeName) {
    for (IRewardTypeName rt : values) {
      if (rt.rewardTypeName().equalsIgnoreCase(rewardTypeName)) {
        return rt;
      }
    }
    return null;
  }
}
