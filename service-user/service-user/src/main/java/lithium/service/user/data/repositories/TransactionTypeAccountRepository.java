package lithium.service.user.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.TransactionTypeAccount;

public interface TransactionTypeAccountRepository extends PagingAndSortingRepository<TransactionTypeAccount, Long>, JpaSpecificationExecutor<Address> {
	TransactionTypeAccount findByAccountTypeCode(String accountTypeCode);
}