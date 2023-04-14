package lithium.service.cashier.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;

public interface ProcessorAccountStatusRepository extends FindOrCreateByNameRepository<ProcessorAccountStatus, Integer> {
    default ProcessorAccountStatus findOne(Integer id) {
        return findById(id).orElse(null);
    }

}
