package lithium.service.casino.data.projection.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRulesFreespinGames;
import lithium.service.casino.data.projection.entities.BonusRulesFreespinGamesProjection;

public interface BonusRulesFreespinGamesProjectionRepository extends PagingAndSortingRepository<BonusRulesFreespinGames, Long> {
	List<BonusRulesFreespinGamesProjection> findByBonusRulesFreespinsBonusRevisionId(Long bonusRevisionId);
}