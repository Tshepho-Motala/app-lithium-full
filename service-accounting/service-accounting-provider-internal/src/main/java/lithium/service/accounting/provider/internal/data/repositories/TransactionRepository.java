package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends LockingPagingSortingRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	//List<Transaction> findByIdGreaterThanAndIdLessThanEqual(long lowerBoundTranIdExclsive, long upperBoundTranIdInclusive);

	//Transaction findFirstByIdGreaterThan(long transactionId);

	Transaction findFirstByIdGreaterThanAndClosedOnLessThan(long transactionId, Date closedOn);

	List<Transaction> findByIdGreaterThanAndIdLessThanEqualAndClosedOnLessThan(long lowerBoundTranIdExclsive, long upperBoundTranIdInclusive, Date closedOn);

	ArrayList<Transaction> findByTransactionTypeIdAndOpenIsFalse(Long tranTypeId);

	List<Transaction> findByIdBetweenOrderByIdAsc(Long lowerInclusive, Long upperInclusive);

	List<Transaction> findByIdIn(List<Long> transactionIdsList);

	Long deleteByIdIn(List<Long> transactionIdsList);

	default Transaction findOne(Long id) {
		return findById(id).orElse(null);
	}
}
