package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.Challenge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface ChallengeRepository extends PagingAndSortingRepository<Challenge, Long>, JpaSpecificationExecutor<Challenge> {
	default Challenge findOne(Long id) {
		return findById(id).orElse(null);
	}
}