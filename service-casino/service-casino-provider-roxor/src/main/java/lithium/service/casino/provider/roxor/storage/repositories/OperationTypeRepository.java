package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.roxor.storage.entities.OperationType;

public interface OperationTypeRepository extends FindOrCreateByCodeRepository<OperationType, Long> {
}
