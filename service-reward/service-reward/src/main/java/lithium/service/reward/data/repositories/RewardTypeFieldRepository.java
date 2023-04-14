package lithium.service.reward.data.repositories;

import lithium.service.reward.data.entities.RewardType;
import lithium.service.reward.data.entities.RewardTypeField;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardTypeFieldRepository extends PagingAndSortingRepository<RewardTypeField, Long>, JpaSpecificationExecutor<RewardTypeField> {

  RewardTypeField findByRewardTypeAndName (RewardType rewardType, String name);
}
