package lithium.service.reward.provider.casino.blueprint.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderConfig implements Serializable {
  private static final long serialVersionUID = -3557868937556391581L;

  private String rewardsBaseUrl;
  private String rewardApiToken;
  private String brandId;
  private String countryCode;
  private String jurisdiction;
  private String iforiumPlatformKey;
  private String playerOffset;
  private String playerGuidPrefix;
}
