package lithium.service.cashier.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.cashier.data.entities.ProcessorAccountType;

public interface ProcessorAccountTypeRepository extends FindOrCreateByNameRepository<ProcessorAccountType, Integer> {
}
