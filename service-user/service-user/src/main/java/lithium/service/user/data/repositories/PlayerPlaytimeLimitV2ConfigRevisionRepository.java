package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.PlayerPlaytimeLimitV2ConfigRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerPlaytimeLimitV2ConfigRevisionRepository extends PagingAndSortingRepository<PlayerPlaytimeLimitV2ConfigRevision, Long>,
    JpaSpecificationExecutor<PlayerPlaytimeLimitV2ConfigRevision> {

}
