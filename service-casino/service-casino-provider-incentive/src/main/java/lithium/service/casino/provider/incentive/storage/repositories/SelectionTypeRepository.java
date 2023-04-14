package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.incentive.storage.entities.SelectionType;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import org.springframework.cache.annotation.Cacheable;

public interface SelectionTypeRepository extends FindOrCreateByCodeRepository<SelectionType, Long> {

}