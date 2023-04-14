package lithium.service.casino.data.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import lithium.service.casino.data.entities.PlayerBonusHistory;

public interface PlayerBonusHistoryRepository extends PagingAndSortingRepository<PlayerBonusHistory, Long>, JpaSpecificationExecutor<PlayerBonusHistory> {
	List<PlayerBonusHistory> findByPlayerBonusPlayerGuidAndBonusBonusCode(String playerGuid, String bonusCode);
	Page<PlayerBonusHistory> findByPlayerBonusPlayerGuid(String playerGuid, Pageable pageable);
	PlayerBonusHistory findByPlayerBonusPlayerGuidAndCompletedFalseAndCancelledFalseAndExpiredFalse(String playerGuid);
	List<PlayerBonusHistory> findByPlayerBonusPlayerGuidAndStartedDateBetweenAndBonusBonusCode(String playerGuid, Date rangeStart, Date rangeEnd, String bonusCode);
	List<PlayerBonusHistory> findByPlayerBonusPlayerGuidAndStartedDateBetween(String playerGuid, Date rangeStart, Date rangeEnd);
	Optional<PlayerBonusHistory> findTop1ByBonusIdAndCompletedFalseAndExpiredFalseAndAndCancelledFalse(Long bonusRevisionId);
	Optional<PlayerBonusHistory> findByRequestIdAndClientId(Long requestId, String clientId);
	//Hourly
	List<PlayerBonusHistory> findByPlayerBonusPlayerGuidAndStartedDateGreaterThanAndBonusBonusCode(String playerGuid, Date start, String bonusCode);
	List<PlayerBonusHistory> findByPlayerBonusPlayerGuidAndCompletedFalseAndExpiredFalseAndAndCancelledFalse(String playerGuid);
	Page<PlayerBonusHistory> findByStartedDateBeforeAndCompletedFalseAndExpiredFalseAndCancelledFalseAndBonusEnabledTrueAndBonusBonusTypeAndBonusBonusTriggerType(Date afterDate, Integer bonusType, Integer bonusTriggerType, Pageable pageRequest);

	default PlayerBonusHistory findOne(Long id) {
		return findById(id).orElse(null);
	}

}
