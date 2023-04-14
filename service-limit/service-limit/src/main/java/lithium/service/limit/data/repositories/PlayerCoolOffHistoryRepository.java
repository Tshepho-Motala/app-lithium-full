package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerCoolOffHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerCoolOffHistoryRepository extends PagingAndSortingRepository<PlayerCoolOffHistory, Long>, JpaSpecificationExecutor<PlayerCoolOffHistory> {
}
