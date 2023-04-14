package lithium.service.cashier.data.repositories;

import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionTag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TransactionTagRepository extends PagingAndSortingRepository<TransactionTag, Long> {
    Optional<TransactionTag> findByTransactionAndType(Transaction transaction, TransactionTagType type);
}
