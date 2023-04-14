package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SummaryDomainTransactionTypeRepository extends LockingPagingSortingRepository<SummaryDomainTransactionType, Long>, JpaSpecificationExecutor<SummaryDomainTransactionType> {
	SummaryDomainTransactionType findByPeriodAndAccountCodeAndTransactionTypeAndCurrency(Period period, AccountCode accountCode, TransactionType transactionType, Currency currency);
	
	List<SummaryDomainTransactionType> findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStart(
		int granularity, String domain, String accountCode, String transactionType, String currency
	);

	List<SummaryDomainTransactionType> findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeInAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStart(
		int granularity, String domain, String[] accountCodes, String transactionType, String currency
	);
	
	List<SummaryDomainTransactionType> findTop3ByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStartDesc(
		int granularity, String domain, String accountCode, String transactionType, String currency
	);
	
	@Modifying
	@Query("update #{#entityName} o set o.tag = :tag where o.period = :period")
	int updateTag(@Param("period") Period period, @Param("tag") int tag);
	
	int deleteByPeriodAndTag(Period period, int tag);
	
	List<SummaryDomainTransactionType> findByPeriodIn(List<Period> periods, Pageable pageable);
}
