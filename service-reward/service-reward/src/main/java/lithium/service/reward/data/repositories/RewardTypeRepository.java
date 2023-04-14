package lithium.service.reward.data.repositories;

import lithium.service.reward.data.entities.RewardType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RewardTypeRepository extends PagingAndSortingRepository<RewardType, Long>, JpaSpecificationExecutor<RewardType> {

  RewardType findByUrlAndName (String url, String name);
  List<RewardType> findByUrl(String providerGuid);
}
