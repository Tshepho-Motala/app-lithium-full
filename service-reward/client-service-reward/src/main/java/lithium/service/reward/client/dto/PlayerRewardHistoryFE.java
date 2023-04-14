package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
public class PlayerRewardHistoryFE implements Serializable {

  private Long id;

  private String awardedDate;
  private String redeemedDate;
  private String expiryDate;

  private String playerGuid;

  private String rewardCode;
  private String rewardName;
  private String status; //PlayerRewardHistoryStatus

  private List<PlayerRewardComponentHistoryFE> rewardComponents;

}
