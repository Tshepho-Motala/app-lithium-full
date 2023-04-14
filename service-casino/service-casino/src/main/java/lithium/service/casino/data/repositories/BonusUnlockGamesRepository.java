package lithium.service.casino.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusUnlockGames;

public interface BonusUnlockGamesRepository extends PagingAndSortingRepository<BonusUnlockGames, Long> {
	List<BonusUnlockGames> findByBonusRevisionId(Long bonusRevisionId);

	@Modifying
	@Transactional
	void deleteByBonusRevisionId(Long bonusRevisionId);
	@Modifying
	@Transactional
	void deleteByGameId(Long gameId);
	@Modifying
	@Transactional
	void deleteByGameGuid(String gameGuid);
}