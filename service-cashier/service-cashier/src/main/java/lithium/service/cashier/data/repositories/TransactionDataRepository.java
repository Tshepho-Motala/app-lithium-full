package lithium.service.cashier.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionData;

public interface TransactionDataRepository extends PagingAndSortingRepository<TransactionData, Long> {
	TransactionData findByTransactionAndFieldAndStageAndOutput(Transaction t, String f, Integer stage, boolean output);
	TransactionData findByTransactionIdAndFieldAndStageAndOutput(Long transactionId, String f, Integer stage, boolean output);
	List<TransactionData> findByTransactionAndOutput(Transaction t, boolean output);
	List<TransactionData> findByTransactionAndStageOrderByStage(Transaction transaction, Integer stage);
	List<TransactionData> findByTransactionOrderByStage(Transaction transaction);
}