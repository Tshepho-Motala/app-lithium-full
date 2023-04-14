package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.ChallengeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

}
