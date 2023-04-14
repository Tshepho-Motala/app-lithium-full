package lithium.service.casino.data.projection.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.projection.entities.PlayerBonusProjection;

public interface PlayerBonusProjectionRepository extends PagingAndSortingRepository<PlayerBonus, Long> {
	PlayerBonusProjection findByPlayerGuidAndCurrentNotNull(String playerGuid);
}