package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPromotionChallengeGroupRepository extends PagingAndSortingRepository<UserPromotionChallengeGroup, Long> {
  default UserPromotionChallengeGroup findOne(Long id) {
    return findById(id).orElse(null);
  }
}
