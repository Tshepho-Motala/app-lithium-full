package lithium.service.cashier.mock.smartcash.data.repositories;

import lithium.service.cashier.mock.smartcash.data.entities.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    Transaction findByReference(String reference);
}
