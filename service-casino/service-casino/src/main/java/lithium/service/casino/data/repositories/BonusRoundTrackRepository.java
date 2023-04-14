package lithium.service.casino.data.repositories;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRoundTrack;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;

public interface BonusRoundTrackRepository extends PagingAndSortingRepository<BonusRoundTrack, Long> {
	BonusRoundTrack findByPlayerBonusHistoryPlayerBonusPlayerGuidAndGameGuidAndRoundId(final String playerGuid, final String gameGuid, final String roundId);

	ArrayList<BonusRoundTrack> findByPlayerBonusHistoryAndCompletedFalse(PlayerBonusHistory playerBonusHistory);
	
	Long countByPlayerBonusHistoryAndCompletedFalse(PlayerBonusHistory playerBonusHistory);

	Optional<BonusRoundTrack> findByPlayerBonusAndGameGuidAndRoundId(PlayerBonus playerBonus, String gameGuid, String roundId);
}