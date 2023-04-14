package lithium.service.reward.provider.casino.roxor.config;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum ProviderConfigProperties implements Serializable {
  WEBSITE("website"),
  REWARDS_URL("rewardsUrl"),
  REWARDS_DEFAULT_DURATION_IN_HOURS("rewardsDefaultDurationInHours"),
  USE_PLAYER_API_TOKEN("usePlayerApiToken");

  @Getter
  @Accessors( fluent = true )
  private String value;
}