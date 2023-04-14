package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.Reward;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RewardRepository extends PagingAndSortingRepository<Reward, Long>, JpaSpecificationExecutor<Reward> {

  default void delete(Long id) {
    deleteById(id);
  }
}
