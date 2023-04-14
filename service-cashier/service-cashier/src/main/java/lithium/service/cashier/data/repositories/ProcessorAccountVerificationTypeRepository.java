package lithium.service.cashier.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.cashier.data.entities.ProcessorAccountVerificationType;

public interface ProcessorAccountVerificationTypeRepository extends FindOrCreateByNameRepository<ProcessorAccountVerificationType, Integer> {
}
