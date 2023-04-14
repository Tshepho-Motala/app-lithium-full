package lithium.service.reward.data.repositories;

import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.data.entities.RewardRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardRepository extends PagingAndSortingRepository<Reward, Long>, JpaSpecificationExecutor<Reward> {

  Reward findByDomainAndCurrentCode(Domain domain, String rewardCode);
  Reward findByDomainAndEditCode(Domain domain, String rewardCode);
  Reward findByDomainAndCurrentId(Domain domain, Long id);
}
