package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.LabelValue;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.domain.summary.storage.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SummaryDomainLabelValueRepository extends JpaRepository<SummaryDomainLabelValue, Long>,
		JpaSpecificationExecutor<SummaryDomainLabelValue> {
	SummaryDomainLabelValue findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(String shard,
			AccountCode accountCode, TransactionType tranactionType, Currency currency, Period period,
			LabelValue labelValue);

	List<SummaryDomainLabelValue> findByAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(
			AccountCode accountCode, TransactionType transactionType, Currency currency, Period period,
			LabelValue labelValue);
}
