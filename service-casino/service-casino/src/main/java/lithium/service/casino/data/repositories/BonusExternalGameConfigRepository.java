package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusExternalGameConfig;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusExternalGameConfigRepository extends PagingAndSortingRepository<BonusExternalGameConfig, Long> {
	List<BonusExternalGameConfig> findByBonusRevisionId(Long bonusRevisionId);
}
