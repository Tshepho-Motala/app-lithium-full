package lithium.service.cashier.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionProcessingAttempt;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;

public interface TransactionProcessingAttemptRepository extends PagingAndSortingRepository<TransactionProcessingAttempt, Long> {
	List<TransactionProcessingAttempt> findByTransactionOrderByTimestampDesc(Transaction transaction);
	TransactionProcessingAttempt findByTransactionAndWorkflowTo(Transaction transaction, TransactionWorkflowHistory transactionWorkflowHistory);
	TransactionProcessingAttempt findByTransactionAndWorkflowFrom(Transaction transaction, TransactionWorkflowHistory transactionWorkflowHistory);
	TransactionProcessingAttempt findTopByTransactionOrderByIdDesc(Transaction transaction);

	List<TransactionProcessingAttempt> findAllByTimestampBeforeAndCleanedFalse(Date date, Pageable pageable);
}
