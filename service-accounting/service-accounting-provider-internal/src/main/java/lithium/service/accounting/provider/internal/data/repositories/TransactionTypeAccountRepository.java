package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeAccount;

public interface TransactionTypeAccountRepository extends PagingAndSortingRepository<TransactionTypeAccount, Long> {
	List<TransactionTypeAccount> findByTransactionType(TransactionType transactionType);
	TransactionTypeAccount findByTransactionTypeAndAccountTypeCode(TransactionType transactionType, String accountTypeCode);
}
