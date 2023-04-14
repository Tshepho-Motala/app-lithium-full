package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SummaryDomainRepository extends LockingPagingSortingRepository<SummaryDomain, Long>, JpaSpecificationExecutor<SummaryDomain> {

	SummaryDomain findByPeriodAndAccountCodeAndCurrency(Period period, AccountCode accountCode, Currency currency);
	
	List<SummaryDomain> findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeOrderByPeriodDateStart(
			int granularity, String domain, String accountCode, String currency);
	SummaryDomain findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(
			int granularity, String domain, String accountCode, String currency, Date startDate);
		
	@Modifying
	@Query("update #{#entityName} o set o.tag = :tag where o.period = :period")
	int updateTag(@Param("period") Period period, @Param("tag") int tag);
	
	int deleteByPeriodAndTag(Period period, int tag);

	List<SummaryDomain> findByPeriodIn(List<Period> periods, Pageable pageable);

	default SummaryDomain findOne(Long id) {
		return findById(id).orElse(null);
	}

}
