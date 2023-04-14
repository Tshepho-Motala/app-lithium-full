package lithium.service.casino.data.projection.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.PlayerBonusFreespinHistory;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;

public interface PlayerBonusFreespinHistoryProjectionRepository extends PagingAndSortingRepository<PlayerBonusFreespinHistory, Long> {
	PlayerBonusFreespinHistoryProjection findByPlayerBonusHistoryId(Long playerBonusHistoryId);
}