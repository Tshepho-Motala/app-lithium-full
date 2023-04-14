package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeTransactionTypeGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountTransactionTypeGroup;

public interface SummaryAccountTransactionTypeRepository extends LockingPagingSortingRepository<SummaryAccountTransactionType, Long>, JpaSpecificationExecutor<SummaryAccountTransactionType> {
	
	SummaryAccountTransactionType findByPeriodIdAndAccountIdAndTransactionTypeCode(Long periodId, Long accountId, String transactionTypeCode);
	SummaryAccountTransactionType findByAccountAccountCodeCodeAndAccountOwnerGuidAndAccountDomainNameAndPeriodGranularityAndAccountCurrencyCode(String accountCode, String ownerGuid, String domainName, int granularity, String currencyCode);

	@Query("select o from #{#entityName} o where o.period = :period and o.account = :account and o.transactionType = :transactionType")
	@Lock(LockModeType.OPTIMISTIC)
	SummaryAccountTransactionType findByPeriodAndAccountAndTransactionTypeForUpdate(@Param("period") Period period, @Param("account") Account account, @Param("transactionType") TransactionType transactionType);
	SummaryAccountTransactionType findByPeriodAndAccountAndTransactionType(Period period, Account account, TransactionType transactionType);

	@Modifying
	@Query(
		"update #{#entityName} o " +
		"set o.debitCents = o.debitCents + :debitCents, o.creditCents = o.creditCents + :creditCents, " +
		"o.tranCount = o.tranCount + :tranCount " +
		"where o.period = :period and o.account = :account and o.transactionType = :transactionType"
	)
	int adjust(
		@Param("period") Period period,
		@Param("account") Account account,
		@Param("transactionType") TransactionType transactionType,
		@Param("debitCents") Long debitCents,
		@Param("creditCents") Long creditCents,
		@Param("tranCount") Long tranCount
	);
	
	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountTransactionTypeGroup"
			+ "(o.period, o.transactionType, o.account.accountCode, o.account.currency, "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period "
			+ "group by o.period, o.transactionType, o.account.accountCode, o.account.currency")
	List<SummaryAccountTransactionTypeGroup> groupBy(@Param("period") Period period);

	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountTransactionTypeGroup"
			+ "(o.period, o.transactionType, o.account.accountCode, o.account.currency, "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period and o.transactionType = :transactionType and o.account.accountCode = :accountCode and o.account.currency = :currency "
			+ "group by o.period, o.transactionType, o.account.accountCode, o.account.currency")
	SummaryAccountTransactionTypeGroup groupBy(@Param("period") Period period, @Param("accountCode") AccountCode accountCode, @Param("transactionType") TransactionType transactionType, @Param("currency") Currency currency);

//	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeTransactionTypeGroup"
//			+ "(o.period, o.transactionType, o.account.accountCode, o.account.currency) from #{#entityName} o "
//			+ "where o.damaged = true "
//			+ "group by o.period, o.transactionType, o.account.accountCode, o.account.currency")
//	List<PeriodAccountCodeTransactionTypeGroup> findDamaged();
//	
//	@Modifying
//	@Query("update #{#entityName} o set o.damaged = false where o.damaged = true")
//	int resetDamaged();

	List<SummaryAccountTransactionType> findByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeAndTransactionTypeCodeAndAccountCurrencyCodeOrderByPeriodDateStart(
		String ownerGuid, int granularity, String domain, String accountCode, String transactionType, String currency
	);

	List<SummaryAccountTransactionType> findByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeInAndTransactionTypeCodeAndAccountCurrencyCodeOrderByPeriodDateStart(
		String ownerGuid, int granularity, String domain, String[] accountCodes, String transactionType, String currency
	);

	@Query("select new lithium.service.accounting.objects.TransactionType " +
			"(s.transactionType.id, s.transactionType.version, s.transactionType.code) " +
			"from SummaryAccountTransactionType s  " +
			"WHERE s.account.owner.guid = :userGuid and s.account.accountType.code = :accountCode AND(s.creditCents <> 0 OR s.debitCents <> 0)" +
			"group by s.transactionType.id")
	List<lithium.service.accounting.objects.TransactionType> findDistinctTransactionTypesUsedByUserByAccountCode(@Param("userGuid") String userGuid, @Param("accountCode") String accountCode);

	SummaryAccountTransactionType findByAccountOwnerGuidAndAccountAccountCodeCodeAndTransactionTypeCodeAndPeriodGranularity(String ownerGuid, String accountCode, String transactionType, int periodGranularity);

    List<SummaryAccountTransactionType> findByAccountOwnerGuidAndAccountAccountCodeCodeAndTransactionTypeCodeInAndPeriodGranularity(String ownerGuid, String accountCode, List<String> transactionType, int periodGranularity);
}
