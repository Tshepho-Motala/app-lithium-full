package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesInstantReward;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusRulesInstantRewardRepository extends PagingAndSortingRepository<BonusRulesInstantReward, Long> {
        List<BonusRulesInstantReward> findByBonusRevisionId(Long bonusRevisionId);
        BonusRulesInstantReward findByBonusRevisionIdAndProvider(Long bonusRevisionId, String provider);
}
