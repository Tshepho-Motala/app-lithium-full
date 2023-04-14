package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionEntryRepository extends PagingAndSortingRepository<TransactionEntry, Long>, JpaSpecificationExecutor<TransactionEntry> {
	List<TransactionEntry> findByTransactionId(Long transactionId);
	Page<TransactionEntry> findByAccountOwnerGuidAndDateIsBetween(String userGuid, Date startDate, Date endDate, Pageable pageable);
	Page<TransactionEntry> findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCode(String userGuid, Date startDate, Date endDate, String accountTypeCode, Pageable pageable);

	Page<TransactionEntry> findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCodeAndAccountCurrencyCodeOrderByIdDesc(String userGuid, Date startDate, Date endDate, String accountTypeCode, String currencyCode, Pageable pageable);

	Page<TransactionEntry> findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCodeAndAccountCurrencyCodeAndAccountAccountCodeCodeNotInAndTransactionTransactionTypeCodeNotInOrderByIdDesc(String userGuid, Date startDate, Date endDate, String accountTypeCode, String currencyCode, List<String> excludedAccountCodes, List<String> excludedTransactionTypes, Pageable pageable);

	Long countByAccount_OwnerAndAccount_DomainAndAccount_CurrencyAndAccount_AccountCode_CodeAndAccount_AccountType_Code (User owner, Domain domain, Currency currency, String accountCode, String accountType);
	Page<TransactionEntry> findByAccountAndDateIsBetweenOrderById(Account account, Date startDate, Date endDate, Pageable pageable);
	Page<TransactionEntry> findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCodeAndAccountCurrencyCodeOrderById(String userGuid, Date startDate, Date endDate, String accountTypeCode, String currencyCode, Pageable pageable);

	Long deleteAllByTransactionIdIn(List<Long> transactionsList);
}
