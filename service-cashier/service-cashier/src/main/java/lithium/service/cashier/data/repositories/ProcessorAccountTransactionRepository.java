package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.ProcessorAccountTransaction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProcessorAccountTransactionRepository extends PagingAndSortingRepository<ProcessorAccountTransaction, Long> {
    default ProcessorAccountTransaction findOne(Long id) {
        return findById(id).orElse(null);
    }

}
