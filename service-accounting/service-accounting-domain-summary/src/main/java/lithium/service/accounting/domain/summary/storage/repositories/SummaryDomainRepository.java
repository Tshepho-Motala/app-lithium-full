package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomain;
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
		findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeOrderByPeriodDateStart(
			int granularity, String domainName, String accountCode, String currency);

	SummaryDomain findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(
			int granularity, String domainName, String accountCode, String currency, Date dateStart);

	List<SummaryDomain> findByPeriodAndAccountCodeAndCurrency(Period period, AccountCode accountCode,
			Currency currency);

	SummaryDomain findByShardAndAccountCodeAndCurrencyAndPeriod(String shard, AccountCode accountCode,
			Currency currency, Period period);

	Page<SummaryDomain> findByAccountCodeAndCurrencyAndPeriodDomainAndPeriodGranularityAndPeriodDateStartAfterOrderByPeriodDateStartAsc(
			AccountCode accountCode, Currency currency, Domain domain, Integer granularity,
			Date dateStartAfter, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from #{#entityName} o where o.id = :id")
	SummaryDomain findByIdForUpdate(@Param("id") Long id);
}
