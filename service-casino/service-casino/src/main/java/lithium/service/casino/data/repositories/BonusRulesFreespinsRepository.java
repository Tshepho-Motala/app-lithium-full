package lithium.service.casino.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRulesFreespins;

public interface BonusRulesFreespinsRepository extends PagingAndSortingRepository<BonusRulesFreespins, Long> {
//	BonusRulesFreespins findByBonusRevisionIdAndGameGuid(Long bonusRevisionId, String gameGuid);
	List<BonusRulesFreespins> findByBonusRevisionId(Long bonusRevisionId);
	BonusRulesFreespins findByBonusRevisionIdAndProvider(Long bonusRevisionId, String provider);
}