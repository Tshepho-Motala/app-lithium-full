package lithium.service.reward.data.repositories;

import java.util.List;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.RewardRevisionTypeValue;
import lithium.service.reward.data.entities.RewardTypeField;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardRevisionTypeValueRepository extends PagingAndSortingRepository<RewardRevisionTypeValue, Long>,
    JpaSpecificationExecutor<RewardRevisionTypeValue> {

//  List<RewardRevisionTypeValue> findByRewardRevision (RewardRevision rewardRevision);
  RewardRevisionTypeValue findByRewardRevisionTypeIdAndRewardTypeFieldIdAndDeletedFalse(Long rewardRevisionTypeId, Long rewardTypeFieldId);
  List<RewardRevisionTypeValue> findByRewardRevisionTypeIdAndDeletedFalse(Long rewardRevisionTypeId);
}
