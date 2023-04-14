package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesCasinoChipGames;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface BonusRulesCasinoChipGamesRepository extends PagingAndSortingRepository<BonusRulesCasinoChipGames, Long> {
    List<BonusRulesCasinoChipGames> findByBonusRulesCasinoChipId(Long bonusRulesCasinoFreeBetId);
    List<BonusRulesCasinoChipGames> findByBonusRulesCasinoChipBonusRevisionId(Long bonusRevisionId);

    @Modifying
    @Transactional
    void deleteByBonusRulesCasinoChipId(Long bonusRulesFreespinsId);
}
