package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.PlayerBonus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerBonusRepository extends PagingAndSortingRepository<PlayerBonus, Long> {
	PlayerBonus findByPlayerGuid(String playerGuid);
	PlayerBonus findByPlayerGuidAndCurrentNotNull(String playerGuid);
	Page<PlayerBonus> findByCurrentNotNull(Pageable pageRequest);
}
