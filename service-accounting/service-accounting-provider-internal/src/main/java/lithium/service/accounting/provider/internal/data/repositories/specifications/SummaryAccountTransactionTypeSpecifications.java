package lithium.service.accounting.provider.internal.data.repositories.specifications;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountCode_;
import lithium.service.accounting.provider.internal.data.entities.Account_;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Currency_;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Domain_;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.Period_;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType_;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType_;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class SummaryAccountTransactionTypeSpecifications {
	public static Specification<SummaryAccountTransactionType> find(
		final String domainName,
		final String currencyCode,
		final String accountCode, 
		final String transactionTypeCode,
		final int granularity,
		final Date dateStart,
		final Date dateEnd,
		final String ownerGuid
	) {
		return new Specification<SummaryAccountTransactionType>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountTransactionType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				String[] accountCodes = accountCode.split(",");

				Join<SummaryAccountTransactionType, Period> joinPeriod = root.join(SummaryAccountTransactionType_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccountTransactionType, Account> joinAccount = root.join(SummaryAccountTransactionType_.account, JoinType.INNER);
				Join<SummaryAccountTransactionType, TransactionType> joinTransactionType = root.join(SummaryAccountTransactionType_.transactionType, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), ownerGuid));
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, joinAccountCode.get(AccountCode_.code).in(accountCodes));
				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));
				
				return p;
			}
		};
	}

	public static Specification<SummaryAccountTransactionType> findAllByAccountExcludeTranTypes(
		final String domainName,
		final String currencyCode,
		final String accountCode,
		final List<String> excludedTransactionTypeCodes,
		final int granularity,
		final Date dateStart,
		final Date dateEnd,
		final String ownerGuid
	) {
		return new Specification<SummaryAccountTransactionType>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountTransactionType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SummaryAccountTransactionType, Period> joinPeriod = root.join(SummaryAccountTransactionType_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccountTransactionType, Account> joinAccount = root.join(SummaryAccountTransactionType_.account, JoinType.INNER);
				Join<SummaryAccountTransactionType, TransactionType> joinTransactionType = root.join(SummaryAccountTransactionType_.transactionType, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);

				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), ownerGuid));
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, joinTransactionType.get(TransactionType_.code).in(excludedTransactionTypeCodes).not());
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));

				return p;
			}
		};
	}
}