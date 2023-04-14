package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SummaryDomainLabelValueRepository extends LockingPagingSortingRepository<SummaryDomainLabelValue, Long>, JpaSpecificationExecutor<SummaryDomainLabelValue> {

	SummaryDomainLabelValue findByPeriodAndAccountCodeAndTransactionTypeAndCurrencyAndLabelValue(Period period, AccountCode accountCode, TransactionType transactionType, Currency currency, LabelValue labelValue);
	
	@Modifying
	@Query("update #{#entityName} o set o.tag = :tag where o.period = :period")
	int updateTag(@Param("period") Period period, @Param("tag") int tag);
	
	int deleteByPeriodAndTag(Period period, int tag);

	List<SummaryDomainLabelValue> findByPeriodInAndLabelValueLabelNameNotIn(
			List<Period> periods, String[] excludedLabels, Pageable pageable);

	default SummaryDomainLabelValue findOne(Long id) {
		return findById(id).orElse(null);
	}

}
