package lithium.service.reward.provider.casino.roxor;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
  private String website;
  private List<String> gameKey;
  //private List<RewardGameConfig> rewardGameConfigs = null;
  private String playerId;
  private String rewardId;
  private String rewardType;
  private Integer numberOfUnits;
  private Money unitValue;
  private Duration duration;
  private Object metadata;
  private Source source;
  private BonusCash bonusCash;
  private Campaign campaign;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private InstantReward instantReward;

  @Data
  @Builder
  @ToString
  @EqualsAndHashCode
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InstantReward {
    private String currency;
    private BigDecimal unitValue;
    private int remainingUnits;
    private String volatility = "FIXED";
  }
}
