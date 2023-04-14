package lithium.service.cashier.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;

public interface TransactionWorkflowHistoryRepository extends PagingAndSortingRepository<TransactionWorkflowHistory, Long> {
	Long countByTransactionAndAccountingReferenceNotNull(Transaction transaction);
	Page<TransactionWorkflowHistory> findByTransactionOrderByTimestampDesc(Transaction transaction, Pageable pageable);
	List<TransactionWorkflowHistory> findByTransactionOrderByTimestampAsc(Transaction transaction, Pageable pageable);
	Optional<TransactionWorkflowHistory> findFirstByTransactionAndBillingDescriptorNotLikeOrderByTimestampDesc(Transaction transaction, String like);

	Long countByStatusCode(String statusCode);
	Page<TransactionWorkflowHistory> findAllByStatusCode(String statuscode, Pageable pageable);
}
