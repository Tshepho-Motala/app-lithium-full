package lithium.service.reward.data.repositories;

import java.util.List;
import lithium.service.reward.data.entities.RewardRevisionTypeGame;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardRevisionTypeGameRepository extends PagingAndSortingRepository<RewardRevisionTypeGame, Long>,
    JpaSpecificationExecutor<RewardRevisionTypeGame> {

  //  List<RewardRevisionTypeGame> findByRewardTypeAndRewardRevision(RewardType rewardType, RewardRevision rewardRevision);
  List<RewardRevisionTypeGame> findByRewardRevisionTypeIdAndDeletedFalse(Long rewardRevisionTypeId);

  RewardRevisionTypeGame findByRewardRevisionTypeIdAndGuidAndDeletedFalse(Long rewardRevisionTypeId, String gameGuid);
}
