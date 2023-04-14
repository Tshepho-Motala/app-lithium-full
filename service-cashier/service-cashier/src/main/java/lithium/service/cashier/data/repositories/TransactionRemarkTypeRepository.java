package lithium.service.cashier.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.cashier.data.entities.TransactionRemarkType;

public interface TransactionRemarkTypeRepository extends FindOrCreateByNameRepository<TransactionRemarkType, Integer> {
}
