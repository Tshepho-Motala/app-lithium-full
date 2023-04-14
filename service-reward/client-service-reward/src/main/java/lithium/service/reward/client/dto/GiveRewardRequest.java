package lithium.service.reward.client.dto;

import java.io.Serializable;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GiveRewardRequest implements Serializable {

  private String playerGuid;
  private Long rewardId;
  private List<RewardRevisionTypeValueOverride> rewardRevisionTypeValueOverrides;
  private RewardSource rewardSource;
}
