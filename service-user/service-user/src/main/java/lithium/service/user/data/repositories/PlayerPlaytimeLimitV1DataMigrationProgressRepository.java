package lithium.service.user.data.repositories;

import java.util.Optional;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV1DataMigrationProgress;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerPlaytimeLimitV1DataMigrationProgressRepository extends
    PagingAndSortingRepository<PlayerPlaytimeLimitV1DataMigrationProgress, Long>,
    JpaSpecificationExecutor<PlayerPlaytimeLimitV1DataMigrationProgress> {

  Optional<PlayerPlaytimeLimitV1DataMigrationProgress> findFirstByIdGreaterThan(Long id);
}
