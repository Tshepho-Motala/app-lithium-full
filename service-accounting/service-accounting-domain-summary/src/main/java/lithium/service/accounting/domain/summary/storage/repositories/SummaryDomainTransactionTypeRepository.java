package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainTransactionType;
import lithium.service.accounting.domain.summary.storage.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SummaryDomainTransactionTypeRepository extends JpaRepository<SummaryDomainTransactionType, Long>,
		JpaSpecificationExecutor<SummaryDomainTransactionType> {
	List<SummaryDomainTransactionType> findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeInAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStart(
			int granularity, String domain, String[] accountCodes, String transactionType, String currency);

	SummaryDomainTransactionType findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriod(String shard,
			AccountCode accountCode, TransactionType transactionType, Currency currency, Period period);

	List<SummaryDomainTransactionType> findByAccountCodeAndTransactionTypeAndCurrencyAndPeriod(AccountCode accountCode,
			TransactionType transactionType, Currency currency, Period period);
}
