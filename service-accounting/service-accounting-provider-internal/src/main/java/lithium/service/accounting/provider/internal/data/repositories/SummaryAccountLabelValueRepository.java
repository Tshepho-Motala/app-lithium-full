package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueType;

public interface SummaryAccountLabelValueRepository extends LockingPagingSortingRepository<SummaryAccountLabelValue, Long>, JpaSpecificationExecutor<SummaryAccountLabelValue> {

	//ONLY USE THIS AS AN EXISTANCE CHECK TO GET THE ID FOR THE ENTRY. THE OTHER DATA IN HERE WILL BE WRONG (SO TRAN COUNT WILL NOT BE VALID WHEN USING THIS CACHE)
//	@Cacheable(cacheNames="lithium.service.accounting.provider.internal.data.repositories.summaryaccountlabelvalue", key="{#root.args[0].getId(),#root.args[1].getId(),#root.args[2].getId(),#root.args[3].getId()}", unless= "#result == null")
	SummaryAccountLabelValue findByPeriodAndAccountAndTransactionTypeAndLabelValue(Period period, Account account, TransactionType transactionType, LabelValue labelValue);
	
	@Override
//	@CacheEvict(beforeInvocation=true, cacheNames="lithium.service.accounting.provider.internal.data.repositories.summaryaccountlabelvalue", key="{#root.args[0].getPeriod().getId(),#root.args[0].getAccount().getId(),#root.args[0].getTransactionType().getId(),#root.args[0].getLabelValue().getId()}")
//	@Cacheable(cacheNames="lithium.service.accounting.provider.internal.data.repositories.summaryaccountlabelvalue", key="{#root.args[0].getPeriod().getId(),#root.args[0].getAccount().getId(),#root.args[0].getTransactionType().getId(),#root.args[0].getLabelValue().getId()}", unless= "#result == null")
	<S extends SummaryAccountLabelValue> S save(S entity);
	
//	@Query("select o from #{#entityName} o where o.period = :period and o.account = :account and o.transactionType = :transactionType and o.labelValue = :labelValue")
//	@Lock(LockModeType.OPTIMISTIC)
//	SummaryAccountLabelValue findByPeriodAndAccountAndTransactionTypeAndLabelValueForUpdate(@Param("period") Period period, @Param("account") Account account, @Param("transactionType") TransactionType transactionType, @Param("labelValue") LabelValue labelValue);

	@Modifying
	@Query(
		"update #{#entityName} o set o.debitCents = o.debitCents + :debitCents, o.creditCents = o.creditCents + :creditCents, " +
		"o.tranCount = o.tranCount + :tranCount " +
		"where o.period = :period " +
		"and o.account = :account " +
		"and o.transactionType = :transactionType " +
		"and o.labelValue = :labelValue "
//		"where o.id = :id"
	)
	int adjust(
		@Param("period") Period period,
		@Param("account") Account account,
		@Param("transactionType") TransactionType transactionType,
		@Param("labelValue") LabelValue labelValue,
		@Param("debitCents") Long debitCents,
		@Param("creditCents") Long creditCents,
		@Param("tranCount") Long tranCount
	);
	
	@Query(
		"select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueType "
		+"(o.period, o.account, o.transactionType, o.labelValue, o.account.currency, sum(o.debitCents), sum(o.creditCents), sum(o.tranCount)) "
		+"from #{#entityName} o "
		+"where o.period.granularity = :granularity "
		+"and o.transactionType.code = :transactionTypeCode "
		+"and o.account.accountCode.code = :accountCode "
		+"and o.account.owner.guid = :ownerGuid "
		+"and o.account.domain.name = :domainName "
		+"and o.account.currency.code = :currencyCode "
		+"and o.labelValue.value = :labelValue "
		+"and o.labelValue.label.name = :labelName "
		+"group by o.period, o.account, o.transactionType, o.labelValue, o.account.currency"
	)
	SummaryAccountLabelValueType summaryAccountLabelValueType(
		@Param("granularity") int granularity,
		@Param("transactionTypeCode") String transactionTypeCode,
		@Param("accountCode") String accountCode,
		@Param("ownerGuid") String ownerGuid,
		@Param("domainName") String domainName,
		@Param("currencyCode") String currencyCode,
		@Param("labelValue") String labelValue,
		@Param("labelName") String labelName
	);
	
	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueGroup"
			+ "(o.period, o.transactionType, o.labelValue, o.account.accountCode, o.account.currency, "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period "
			+ "group by o.period, o.labelValue, o.transactionType, o.account.accountCode, o.account.currency")
	List<SummaryAccountLabelValueGroup> groupBy(@Param("period") Period period);

	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueGroup"
			+ "(o.period, o.transactionType, o.labelValue, o.account.accountCode, o.account.currency, "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period and o.transactionType = :transactionType and o.labelValue = :labelValue and o.account.accountCode = :accountCode and o.account.currency = :currency "
			+ "group by o.period, o.labelValue, o.transactionType, o.account.accountCode, o.account.currency")
	SummaryAccountLabelValueGroup groupBy(@Param("period") Period period, @Param("accountCode") AccountCode accountCode, @Param("transactionType") TransactionType transactionType, @Param("currency") Currency currency, @Param("labelValue") LabelValue labelValue);

//	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeLabelValueGroup"
//			+ "(o.period, o.transactionType, o.labelValue, o.account.accountCode, o.account.currency) from #{#entityName} o "
//			+ "where o.damaged = true "
//			+ "group by o.period, o.labelValue, o.transactionType, o.account.accountCode, o.account.currency")
//	List<PeriodAccountCodeLabelValueGroup> findDamaged();
//	
//	@Modifying
//	@Query("update #{#entityName} o set o.damaged = false where o.damaged = true")
//	int resetDamaged();

}
