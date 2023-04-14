package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.domain.v2.storage.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SummaryDomainLabelValueRepository extends JpaRepository<SummaryDomainLabelValue, Long>,
		JpaSpecificationExecutor<SummaryDomainLabelValue> {
	SummaryDomainLabelValue findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValueAndTestUsers(String shard,
		 AccountCode accountCode, TransactionType tranactionType, Currency currency, Period period,
		 LabelValue labelValue, boolean isTestUser);

	List<SummaryDomainLabelValue> findByAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(
			AccountCode accountCode, TransactionType transactionType, Currency currency, Period period,
			LabelValue labelValue);
}
