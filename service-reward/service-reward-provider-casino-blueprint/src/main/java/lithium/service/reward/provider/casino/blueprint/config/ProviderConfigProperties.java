package lithium.service.reward.provider.casino.blueprint.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum ProviderConfigProperties implements Serializable {
  REWARDS_BASE_URL("rewardsBaseUrl"),
  REWARDS_API_TOKEN("rewardsApiToken"),
  BRAND_ID("brandId"),
  COUNTRY_CODE("countryCode"),
  JURISDICTION("jurisdiction"),
  IFORIUM_PLATFORM_KEY("iforiumPlatformKey"),
  PLAYER_OFFSET("playerOffset"),
  PLAYER_GUID_PREFIX("playerGuidPrefix");
  @Getter
  @Accessors( fluent = true )
  private String value;
}