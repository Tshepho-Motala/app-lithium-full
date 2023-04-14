package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespin;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusRulesInstantRewardFreespinRepository extends PagingAndSortingRepository<BonusRulesInstantRewardFreespin, Long> {
        List<BonusRulesInstantRewardFreespin> findByBonusRevisionId(Long bonusRevisionId);
        BonusRulesInstantRewardFreespin findByBonusRevisionIdAndProvider(Long bonusRevisionId, String provider);
}
