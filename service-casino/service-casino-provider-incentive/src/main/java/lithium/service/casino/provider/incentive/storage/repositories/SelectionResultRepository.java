package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.incentive.storage.entities.SelectionResult;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import org.springframework.cache.annotation.Cacheable;

public interface SelectionResultRepository extends FindOrCreateByCodeRepository<SelectionResult, Long> {

	@Cacheable(value = "lithium.service.casino.provider.incentive.storage.entities.selectionResult.byCode", unless = "#result == null")
	SelectionResult findByCode(String code);

}