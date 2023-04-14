package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.incentive.storage.entities.Currency;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import org.springframework.cache.annotation.Cacheable;

public interface SettlementResultRepository extends FindOrCreateByCodeRepository<SettlementResult, Long> {

	@Cacheable(value = "lithium.service.casino.provider.incentive.storage.entities.settlementresult.byCode", unless = "#result == null")
	SettlementResult findByCode(String code);

}