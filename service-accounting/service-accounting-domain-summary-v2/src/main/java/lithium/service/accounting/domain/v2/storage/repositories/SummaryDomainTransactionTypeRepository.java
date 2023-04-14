package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomainTransactionType;
import lithium.service.accounting.domain.v2.storage.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SummaryDomainTransactionTypeRepository extends JpaRepository<SummaryDomainTransactionType, Long>,
		JpaSpecificationExecutor<SummaryDomainTransactionType> {
	List<SummaryDomainTransactionType> findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeInAndTransactionTypeCodeAndCurrencyCodeAndTestUsersInOrderByPeriodDateStart(
			int granularity, String domain, String[] accountCodes, String transactionType, String currency, List<Boolean> testUsers);

	SummaryDomainTransactionType findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriod(String shard,
			AccountCode accountCode, TransactionType transactionType, Currency currency, Period period);

	SummaryDomainTransactionType findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndTestUsers(String shard,
			 AccountCode accountCode, TransactionType transactionType, Currency currency, Period period, boolean isTestUser);

	List<SummaryDomainTransactionType> findByAccountCodeAndTransactionTypeAndCurrencyAndPeriod(AccountCode accountCode,
			TransactionType transactionType, Currency currency, Period period);
}
