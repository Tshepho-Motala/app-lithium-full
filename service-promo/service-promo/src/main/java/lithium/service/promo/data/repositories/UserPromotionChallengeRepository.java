package lithium.service.promo.data.repositories;

import java.util.List;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPromotionChallengeRepository extends PagingAndSortingRepository<UserPromotionChallenge, Long>, JpaSpecificationExecutor<UserPromotionChallenge> {
	List<UserPromotionChallenge> findByUserPromotion(UserPromotion userPromotion);

	default UserPromotionChallenge findOne(Long id) {
		return findById(id).orElse(null);
	}
}