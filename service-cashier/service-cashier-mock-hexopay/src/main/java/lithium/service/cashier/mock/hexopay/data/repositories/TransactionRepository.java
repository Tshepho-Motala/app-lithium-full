package lithium.service.cashier.mock.hexopay.data.repositories;

import lithium.service.cashier.mock.hexopay.data.entities.Transaction;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    Transaction findByUid(String uid);
    List<Transaction> findByTrackingId(String trackingId);
    List<Transaction> findByTtlNotAndStatus(Long ttl, Status status);
}
