package lithium.service.casino.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.PlayerBonusPending;

public interface PlayerBonusPendingRepository extends PagingAndSortingRepository<PlayerBonusPending, Long> {
	List<PlayerBonusPending> findByPlayerGuidOrderByCreatedDateAsc(String playerGuid);
	Optional<PlayerBonusPending> findTop1ByBonusRevisionId(Long bonusRevisionId);

	default PlayerBonusPending findOne(Long id) {
		return findById(id).orElse(null);
	}

}