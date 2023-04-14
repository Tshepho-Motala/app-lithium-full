package lithium.service.cashier.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.cashier.data.entities.ProcessorAccountTransactionState;

public interface ProcessorAccountTransactionStateRepository extends FindOrCreateByNameRepository<ProcessorAccountTransactionState, Integer> {
}
