package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesCasinoChip;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusRulesCasinoChipRepository extends PagingAndSortingRepository<BonusRulesCasinoChip, Long> {
        List<BonusRulesCasinoChip> findByBonusRevisionId(Long bonusRevisionId);
        BonusRulesCasinoChip findByBonusRevisionIdAndProvider(Long bonusRevisionId, String provider);
        }
