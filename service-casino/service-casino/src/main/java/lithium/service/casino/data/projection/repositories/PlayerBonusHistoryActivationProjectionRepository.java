package lithium.service.casino.data.projection.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.projection.entities.PlayerBonusHistoryActivationProjection;

public interface PlayerBonusHistoryActivationProjectionRepository extends PagingAndSortingRepository<PlayerBonusHistory, Long>, JpaSpecificationExecutor<PlayerBonusHistoryActivationProjection> {
	Page<PlayerBonusHistoryActivationProjection> findByBonusIdAndPlayerBonusPlayerGuidIgnoreCaseContainingAndCompletedOrCancelledOrExpired(Long bonusRevisionId, String playerGuid, Boolean completed, Boolean cancelled, Boolean expired, Pageable pageable);
	
	@Query(value=
		"select pbh from PlayerBonusHistory pbh "
		+"left join pbh.bonus br "
		+"left join pbh.playerBonus pb "
		+"where pbh.bonus.id = :bonusRevisionId "
		+"and pbh.bonus.id = br.id "
		+"and pbh.playerBonus.id = pb.id "
		+"and pbh.playerBonus.playerGuid like :playerGuid "
		+"and ((pbh.completed <> :active and pbh.expired <> :active and pbh.cancelled <> :active) "
		+"or (pbh.completed = :completed and pbh.expired <> :completed and pbh.cancelled <> :completed) "
		+"or (pbh.completed <> :expired and pbh.expired = :expired and pbh.cancelled <> :expired) "
		+"or (pbh.completed <> :cancelled and pbh.expired <> :cancelled and pbh.cancelled = :cancelled)) "
	)
	Page<PlayerBonusHistoryActivationProjection> findByBonusRevisionIdProjection(@Param("bonusRevisionId") Long bonusRevisionId, @Param("playerGuid") String playerGuid, @Param("completed") Boolean completed, @Param("cancelled") Boolean cancelled, @Param("expired") Boolean expired, @Param("active") Boolean active, Pageable pageable);

}