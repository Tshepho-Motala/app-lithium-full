package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerTimeSlotLimitHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerTimeSlotLimitHistoryRepository extends PagingAndSortingRepository<PlayerTimeSlotLimitHistory, Long>, JpaSpecificationExecutor<PlayerTimeSlotLimitHistory> {
}
