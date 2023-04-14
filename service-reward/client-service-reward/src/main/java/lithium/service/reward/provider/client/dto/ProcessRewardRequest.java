package lithium.service.reward.provider.client.dto;

import java.io.Serializable;
import java.util.List;
import lithium.service.reward.client.dto.Domain;
import lithium.service.reward.client.dto.Reward;
import lithium.service.reward.client.dto.RewardRevision;
import lithium.service.reward.client.dto.RewardRevisionTypeGame;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.client.dto.User;
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
public class ProcessRewardRequest implements Serializable {

  private User player;
  //  private Long customValueInCents;
  //  private List<RewardRevisionTypeValue> rewardRevisionTypeValues;
  private List<ProcessRewardTypeValue> processRewardTypeValues;
  private List<RewardRevisionTypeGame> rewardRevisionTypeGames;
  private RewardType rewardType;
  private Reward reward;
  private RewardRevision rewardRevision;
  private Long playerRewardTypeHistoryId;
  private Domain domain;

  public String domainName() {
    return domain.getName();
  }
  public String domainCurrency() {
    return domain.getCurrency();
  }

  public String username() {
    return player.username();
  }

  public String findRewardTypeValue(String rewardTypeFieldName) {
    return processRewardTypeValues.stream().filter(pr -> rewardTypeFieldName.equalsIgnoreCase(pr.getRewardTypeFieldName())).findFirst().get().getRewardRevisionTypeValue();
  }
  public <T> T findRewardTypeValue(String rewardTypeFieldName, Class<T> type) {
    String findRewardTypeValue = findRewardTypeValue(rewardTypeFieldName);
    if (type.isAssignableFrom(Integer.class)) {
      return type.cast(Integer.parseInt(findRewardTypeValue));
    } else if (type.isAssignableFrom(Long.class)) {
      return type.cast(Long.parseLong(findRewardTypeValue));
    }
    return type.cast(findRewardTypeValue);
  }
}
