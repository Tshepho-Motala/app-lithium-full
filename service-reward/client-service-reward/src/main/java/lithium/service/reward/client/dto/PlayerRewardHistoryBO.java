package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
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
public class PlayerRewardHistoryBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 885757756387490963L;
  private Long id;

  private String awardedDate;
  private String redeemedDate;
  private String expiryDate;

  private String playerGuid;

  private String rewardCode;
  private String rewardName;
  private String status; //PlayerRewardHistoryStatus

  private boolean cancellable;

  private List<PlayerRewardTypeHistoryBO> rewardTypes;
}
