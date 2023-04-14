package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerExclusionHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerExclusionHistoryRepository extends PagingAndSortingRepository<PlayerExclusionHistory, Long>, JpaSpecificationExecutor<PlayerExclusionHistory> {
}
