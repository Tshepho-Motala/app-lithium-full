package lithium.service.reward.client.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRewardTypeHistoryBO implements Serializable {

  @Serial
  private static final long serialVersionUID = -2018901401379715710L;
  private Long id;

  private String status; //PlayerRewardComponentStatus
  private String rewardTypeName;

  private Long playerRewardHistoryId;
  private String rewardName;
  private String rewardCode;
  private String playerGuid;
  private String awardedOn;
  private String created;
  private boolean cancellable;

  private BigDecimal typeCounter;
}
