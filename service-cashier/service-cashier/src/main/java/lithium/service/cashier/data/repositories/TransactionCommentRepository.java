package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.TransactionComment;

import java.util.List;

public interface TransactionCommentRepository extends PagingAndSortingRepository<TransactionComment, Long> {
	List<TransactionComment> findByWorkflow(TransactionWorkflowHistory workflowHistory);
}
