package lithium.service.reward.provider.casino.roxor.config;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProviderConfig implements Serializable {
  private static final long serialVersionUID = -3557868937556391581L;

  private String website;
  private String rewardsUrl;
  private Integer rewardsDefaultDurationInHours;
  private Boolean usePlayerApiToken;
}
