package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespinGames;
import lithium.service.casino.data.entities.BonusRulesInstantRewardGames;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface BonusRulesInstantRewardFreespinGamesRepository extends PagingAndSortingRepository<BonusRulesInstantRewardFreespinGames, Long> {
    List<BonusRulesInstantRewardFreespinGames> findByBonusRulesInstantRewardFreespinId(Long bonusRulesInstantRewardFreespinId);
    List<BonusRulesInstantRewardFreespinGames> findByBonusRulesInstantRewardFreespinBonusRevisionId(Long bonusRevisionId);

    @Modifying
    @Transactional
    void deleteByBonusRulesInstantRewardFreespinId(Long bonusRulesFreespinsId);
}
