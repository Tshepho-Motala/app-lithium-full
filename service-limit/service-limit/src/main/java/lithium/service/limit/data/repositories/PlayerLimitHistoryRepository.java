package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerLimitHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerLimitHistoryRepository extends PagingAndSortingRepository<PlayerLimitHistory, Long>, JpaSpecificationExecutor<PlayerLimitHistory> {
}
