package lithium.service.casino.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRulesFreespinGames;

public interface BonusRulesFreespinGamesRepository extends PagingAndSortingRepository<BonusRulesFreespinGames, Long> {
	List<BonusRulesFreespinGames> findByBonusRulesFreespinsId(Long bonusRulesFreespinsId);
	List<BonusRulesFreespinGames> findByBonusRulesFreespinsBonusRevisionId(Long bonusRevisionId);
	
	@Modifying
	@Transactional
	void deleteByBonusRulesFreespinsId(Long bonusRulesFreespinsId);
}