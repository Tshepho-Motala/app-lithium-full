package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.PlayerPlayTimeLimitAdHocReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayTimeLimitAdHocResetRepository extends JpaRepository<PlayerPlayTimeLimitAdHocReset, Long> {

  default PlayerPlayTimeLimitAdHocReset findOne(Long id) {
    return findById(id).orElse(null);
  }
}
