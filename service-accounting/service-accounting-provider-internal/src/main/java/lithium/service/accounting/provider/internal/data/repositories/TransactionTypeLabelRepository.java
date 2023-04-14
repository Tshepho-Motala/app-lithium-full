package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeLabel;

public interface TransactionTypeLabelRepository extends PagingAndSortingRepository<TransactionTypeLabel, Long> {
	List<TransactionTypeLabel> findByTransactionType(TransactionType transactionType);
	List<TransactionTypeLabel> findByTransactionTypeId(Long transactionTypeId);
	TransactionTypeLabel findByTransactionTypeAndLabel(TransactionType transactionType, String label);
}
