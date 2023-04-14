package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.incentive.storage.entities.Selection;
import lithium.service.casino.provider.incentive.storage.entities.SelectionType;

public interface SelectionRepository extends FindOrCreateByGuidRepository<Selection, Long> {

}