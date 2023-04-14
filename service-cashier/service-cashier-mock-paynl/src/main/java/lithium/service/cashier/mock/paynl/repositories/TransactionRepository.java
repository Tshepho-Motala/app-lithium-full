package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    Transaction findTransactionById(String id);
}
