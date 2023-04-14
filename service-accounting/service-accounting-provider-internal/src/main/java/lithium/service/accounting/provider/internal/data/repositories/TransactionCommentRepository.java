package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.TransactionComment;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;

public interface TransactionCommentRepository extends PagingAndSortingRepository<TransactionComment, Long> {
	List<TransactionLabelValue> findByTransactionId(Long transactionId);
}
