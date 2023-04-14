package lithium.service.reward.data.repositories;

import java.util.List;
import java.util.Optional;

import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.RewardType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardRevisionTypeRepository extends PagingAndSortingRepository<RewardRevisionType, Long>,
    JpaSpecificationExecutor<RewardRevisionType> {

  List<RewardRevisionType> findByRewardRevision(RewardRevision rewardRevision);
  Optional<RewardRevisionType> findByRewardRevisionAndRewardType(RewardRevision rewardRevision, RewardType rewardType);
}
