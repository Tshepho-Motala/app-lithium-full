package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesInstantRewardGames;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface BonusRulesInstantRewardGamesRepository extends PagingAndSortingRepository<BonusRulesInstantRewardGames, Long> {
    List<BonusRulesInstantRewardGames> findByBonusRulesInstantRewardId(Long bonusRulesCasinoFreeBetId);
    List<BonusRulesInstantRewardGames> findByBonusRulesInstantRewardBonusRevisionId(Long bonusRevisionId);

    @Modifying
    @Transactional
    void deleteByBonusRulesInstantRewardId(Long bonusRulesFreespinsId);
}
