package lithium.service.reward.data.repositories;

import java.util.List;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.RewardRevisionType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRewardTypeHistoryRepository extends PagingAndSortingRepository<PlayerRewardTypeHistory, Long>,
    JpaSpecificationExecutor<PlayerRewardTypeHistory> {
  PlayerRewardTypeHistory findByRewardRevisionTypeAndPlayerRewardHistory(RewardRevisionType rewardRevisionType, PlayerRewardHistory playerRewardHistory);
  List<PlayerRewardTypeHistory> findByPlayerRewardHistory(PlayerRewardHistory playerRewardHistory);
  List<PlayerRewardTypeHistory> findByPlayerRewardHistoryAndStatusIn(PlayerRewardHistory playerRewardHistory, List<PlayerRewardComponentStatus> playerRewardTypeStatuses);
  PlayerRewardTypeHistory findByReferenceId(String rewardTypeReference);

  default PlayerRewardTypeHistory findOne(Long id) {
    return findById(id).orElse(null);
  }
}