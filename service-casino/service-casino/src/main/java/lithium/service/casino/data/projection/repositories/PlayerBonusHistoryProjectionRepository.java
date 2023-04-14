package lithium.service.casino.data.projection.repositories;

import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.projection.entities.PlayerBonusHistoryProjection;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerBonusHistoryProjectionRepository extends PagingAndSortingRepository<PlayerBonusHistory, Long> {
	PlayerBonusHistoryProjection getById(Long id);

}
