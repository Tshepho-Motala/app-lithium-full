package lithium.service.casino.data.projection.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.casino.data.projection.entities.PlayerBonusPendingProjection;

public interface PlayerBonusPendingProjectionRepository extends PagingAndSortingRepository<PlayerBonusPending, Long> {
	Page<PlayerBonusPendingProjection> findByPlayerGuid(String playerGuid, Pageable pageable);
	Page<PlayerBonusPendingProjection> findByBonusRevision(BonusRevision bonusRevision, Pageable pageable);
}