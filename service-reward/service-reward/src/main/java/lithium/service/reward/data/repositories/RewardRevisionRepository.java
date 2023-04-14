package lithium.service.reward.data.repositories;

import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.data.entities.RewardRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RewardRevisionRepository extends PagingAndSortingRepository<RewardRevision, Long>, JpaSpecificationExecutor<RewardRevision> {

  RewardRevision findByRewardDomainAndCode(Domain domain, String code);

  default RewardRevision findOne(Long id) {
    return findById(id).orElse(null);
  }

  List<RewardRevision> findRewardRevisionsByReward(Reward reward);
}
