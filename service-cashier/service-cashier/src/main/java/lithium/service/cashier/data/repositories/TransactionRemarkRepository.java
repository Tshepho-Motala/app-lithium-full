package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionRemark;
import lithium.service.cashier.data.entities.TransactionRemarkType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransactionRemarkRepository extends PagingAndSortingRepository<TransactionRemark, Long>,
		JpaSpecificationExecutor<TransactionRemark> {
	TransactionRemark findTop1ByTransaction(Transaction transaction);
	List<TransactionRemark> findByTransactionOrderByIdDesc(Transaction transaction);
	TransactionRemark findTop1ByTransactionAndType(Transaction transaction, TransactionRemarkType type);
}
