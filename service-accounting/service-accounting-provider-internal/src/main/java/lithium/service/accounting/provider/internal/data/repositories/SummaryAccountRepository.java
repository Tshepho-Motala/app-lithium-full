package lithium.service.accounting.provider.internal.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountCodeGroup;

public interface SummaryAccountRepository extends LockingPagingSortingRepository<SummaryAccount, Long>, JpaSpecificationExecutor<SummaryAccount> {

	SummaryAccount findByPeriodAndAccount(Period period, Account account);
	
	@Query("select o from #{#entityName} o where o.period = :period and o.account = :account")
	SummaryAccount findByPeriodAndAccountForUpdate(@Param("period") Period period, @Param("account") Account account);
	
	SummaryAccount findFirstByAccountAndPeriodGranularityAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(Account account, int granularity, Date dateStart);

	SummaryAccount findByPeriodIdAndAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCode(Long periodId, String accountCode, String accountType, String ownerGuid, String currencyCode);
	
	SummaryAccount findByAccountAccountCodeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndPeriodGranularityAndPeriodDomainName(String accountCode, String ownerGuid, String currencyCode, int granularity, String domainName);
	List<SummaryAccount> findByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeAndAccountCurrencyCodeOrderByPeriodDateStart(String ownerGuid, int granularity, String domainName, String accountCode, String currencyCode);

	SummaryAccount findFirstByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeAndAccountCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(String ownerGuid, int granularity, String domain, String accountCode, String currencyCode, Date dateStart);

	@Modifying
	@Query("update #{#entityName} o set o.openingBalanceCents = o.openingBalanceCents + :amountCents, "+
		"o.closingBalanceCents = o.closingBalanceCents + :amountCents "+
		" where o.account = :account " +
		" and o.period in (select p from Period p where p.granularity = :granularity and p.dateStart >= :dateStart) ")
	int adjustBalances(@Param("amountCents") Long amountCents, @Param("account") Account account, @Param("granularity") int granularity, @Param("dateStart") Date dateStart);

	@Modifying
	@Query(
		"update #{#entityName} o " +
		"set o.debitCents = o.debitCents + :debitCents, o.creditCents = o.creditCents + :creditCents, " +
		"o.closingBalanceCents = o.closingBalanceCents + :debitCents - :creditCents, o.tranCount = o.tranCount + :tranCount " +
		"where o.period = :period and o.account = :account"
	)
	int adjust(@Param("period") Period period, @Param("account") Account account, @Param("debitCents") Long debitCents, @Param("creditCents") Long creditCents, @Param("tranCount") Long tranCount);

	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountGroup"
			+ "(o.period, o.account.accountCode, o.account.currency, sum(o.openingBalanceCents), "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.closingBalanceCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period group by o.period, o.account.accountCode")
	List<SummaryAccountGroup> groupBy(@Param("period") Period period);

	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountGroup"
			+ "(o.period, o.account.accountCode, o.account.currency, sum(o.openingBalanceCents), "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.closingBalanceCents), sum(o.tranCount)) from #{#entityName} o "
			+ "where o.period = :period and o.account.accountCode = :accountCode and o.account.currency = :currency "
			+ "group by o.period, o.account.accountCode, o.account.currency")
	SummaryAccountGroup groupBy(@Param("period") Period period, @Param("accountCode") AccountCode accountCode, @Param("currency") Currency currency);
	
	@Query("select new lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountCodeGroup"
			+ "(o.period, o.account.accountCode, o.account.currency, "
			+ "sum(o.debitCents), sum(o.creditCents), sum(o.tranCount), o.account.owner) from #{#entityName} o "
			+ "where o.period = :period and o.account.currency = :currency and o.account.owner = :owner "
			+ "group by o.period, o.account.accountCode, o.account.currency, o.account.owner")
	List<SummaryAccountCodeGroup> groupBy(@Param("period") Period period, @Param("currency") Currency currency, @Param("owner") User owner);

	@Query(value = "SELECT COALESCE(SUM(credit_cents - debit_cents) , 0) "
			+ " FROM summary_account acc_sum "
			+ " INNER JOIN account acc ON acc_sum.account_id = acc.id "
			+ " INNER JOIN account_code acc_code ON acc.account_code_id = acc_code.id "
			+ " INNER JOIN user usr ON acc.owner_id = usr.id "
			+ " INNER JOIN period prd ON acc_sum.period_id = prd.id "
			+ " INNER JOIN granularity grn ON prd.granularity = grn.id "
			+ " WHERE acc_code.code IN :accountCodes "
			+ " AND usr.guid = :guid "
			+ " AND grn.name = :granularity "
			+ " AND prd.date_start >= :dateFrom", nativeQuery = true)
	Long getTurnoverFrom(
			@Param("guid") String guid,
			@Param("dateFrom") String dateFrom,
			@Param("accountCodes") List<String> accountCodes,
			@Param("granularity") String granularity
	);

	default SummaryAccount findOne(Long id) {
		return findById(id).orElse(null);
	}

}
