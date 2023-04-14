package lithium.service.reward.data.repositories;

import java.util.Date;
import java.util.List;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRewardHistoryRepository extends PagingAndSortingRepository<PlayerRewardHistory, Long>,
    JpaSpecificationExecutor<PlayerRewardHistory> {

  List<PlayerRewardHistory> findByPlayerAndRewardRevisionAndStatus(User player, RewardRevision rewardRevision, PlayerRewardHistoryStatus status);
  Page<PlayerRewardHistory> findByExpiryDateIsBeforeAndStatusIsIn(Date date, List<PlayerRewardHistoryStatus> playerRewardHistoryStatuses, Pageable pageRequest);
  Page<PlayerRewardHistory> findByRewardRevisionAndStatusIsNotIn(RewardRevision rewardRevision, List<PlayerRewardHistoryStatus> playerRewardHistoryStatuses, Pageable pageRequest);
  List<PlayerRewardHistory> findByRewardRevisionAndStatusIsNotInAndPlayerGuid(RewardRevision rewardRevision, List<PlayerRewardHistoryStatus> playerRewardHistoryStatuses, String playerGuid);

  default PlayerRewardHistory findOne(Long id) {
    return findById(id).orElse(null);
  }
}