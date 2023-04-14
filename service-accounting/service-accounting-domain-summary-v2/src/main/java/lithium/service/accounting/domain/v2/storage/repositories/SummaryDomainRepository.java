package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;

public interface SummaryDomainRepository extends JpaRepository<SummaryDomain, Long>,
		JpaSpecificationExecutor<SummaryDomain> {
	List<SummaryDomain>
	findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndTestUsersInOrderByPeriodDateStart(
			int granularity, String domainName, String accountCode, String currency, List<Boolean> testUsers);

	SummaryDomain findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeAndTestUsersInOrderByPeriodDateStartDesc(
			int granularity, String domainName, String accountCode, String currency, Date dateStart, List<Boolean> testUsers);

	List<SummaryDomain> findByPeriodAndAccountCodeAndCurrency(Period period, AccountCode accountCode,
			Currency currency);

	List<SummaryDomain> findByPeriodAndAccountCodeAndCurrencyAndTestUsersIn(Period period, AccountCode accountCode,
																		  Currency currency, List<Boolean> testUsers);

	SummaryDomain findByShardAndAccountCodeAndCurrencyAndPeriodAndTestUsers(String shard, AccountCode accountCode,
																			  Currency currency, Period period,  boolean testUsers);

	List<SummaryDomain> findByAccountCodeAndCurrencyAndPeriodAndTestUsers(AccountCode accountCode, Currency currency,
															  Period period, boolean isTestUser);

	Page<SummaryDomain> findByAccountCodeAndCurrencyAndPeriodDomainAndPeriodGranularityAndPeriodDateStartAfterAndTestUsersOrderByPeriodDateStartAsc(
			AccountCode accountCode, Currency currency, Domain domain, Integer granularity,
			Date dateStartAfter,boolean testUsers, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from #{#entityName} o where o.id = :id")
	SummaryDomain findByIdForUpdate(@Param("id") Long id);
}
