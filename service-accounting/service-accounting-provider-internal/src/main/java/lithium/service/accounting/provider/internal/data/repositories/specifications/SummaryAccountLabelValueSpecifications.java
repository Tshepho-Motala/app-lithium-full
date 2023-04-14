package lithium.service.accounting.provider.internal.data.repositories.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountCode_;
import lithium.service.accounting.provider.internal.data.entities.Account_;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Currency_;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Domain_;
import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.LabelValue_;
import lithium.service.accounting.provider.internal.data.entities.Label_;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.Period_;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue_;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType_;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.entities.User_;

public class SummaryAccountLabelValueSpecifications {
	public static Specification<SummaryAccountLabelValue> find(
		final String domainName,
		final String currencyCode,
		final String accountCode, 
		final String transactionTypeCode,
		final int granularity,
		final Date dateStart,
		final Date dateEnd,
		final String ownerGuid,
		final String labelName,
		final String labelValue
	) {
		return new Specification<SummaryAccountLabelValue>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountLabelValue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SummaryAccountLabelValue, LabelValue> joinLabelValue = root.join(SummaryAccountLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);
				Join<SummaryAccountLabelValue, Period> joinPeriod = root.join(SummaryAccountLabelValue_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccountLabelValue, Account> joinAccount = root.join(SummaryAccountLabelValue_.account, JoinType.INNER);
				Join<SummaryAccountLabelValue, TransactionType> joinTransactionType = root.join(SummaryAccountLabelValue_.transactionType, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), ownerGuid));
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));
				
				return p;
			}
		};
	}
	
	public static Specification<SummaryAccountLabelValue> find(
		final String domainName,
		final Long periodId,
		final String accountCode,
		final String transactionTypeCode,
		final String labelValue,
		final String labelName,
		final String currencyCode
	) {
		return new Specification<SummaryAccountLabelValue>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountLabelValue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SummaryAccountLabelValue, LabelValue> joinLabelValue = root.join(SummaryAccountLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);
				Join<SummaryAccountLabelValue, Period> joinPeriod = root.join(SummaryAccountLabelValue_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccountLabelValue, Account> joinAccount = root.join(SummaryAccountLabelValue_.account, JoinType.INNER);
				Join<SummaryAccountLabelValue, TransactionType> joinTransactionType = root.join(SummaryAccountLabelValue_.transactionType, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.id), periodId));
				
				return p;
			}
		};
	}

	public static Specification<SummaryAccountLabelValue> find(
			final String domainName,
			final Long periodId,
			final String accountCode,
			final List<String> transactionTypeCodes,
			final String labelValue,
			final String labelName,
			final String currencyCode
	) {
		return new Specification<SummaryAccountLabelValue>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountLabelValue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SummaryAccountLabelValue, LabelValue> joinLabelValue = root.join(SummaryAccountLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);
				Join<SummaryAccountLabelValue, Period> joinPeriod = root.join(SummaryAccountLabelValue_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccountLabelValue, Account> joinAccount = root.join(SummaryAccountLabelValue_.account, JoinType.INNER);
				Join<SummaryAccountLabelValue, TransactionType> joinTransactionType = root.join(SummaryAccountLabelValue_.transactionType, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);

				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, joinTransactionType.get(TransactionType_.code).in(transactionTypeCodes));
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.id), periodId));

				return p;
			}
		};
	}

	public static Specification<SummaryAccountLabelValue> find(Period period, String labelName, List<String> labelValues, String userGuid) {
		return new Specification<SummaryAccountLabelValue>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccountLabelValue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SummaryAccountLabelValue, LabelValue> joinLabelValue = root.join(SummaryAccountLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);

				Join<SummaryAccountLabelValue, Account> joinAccount = root.join(SummaryAccountLabelValue_.account, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);

				Predicate p = cb.equal(root.get(SummaryAccountLabelValue_.period), period);
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, joinLabelValue.get(LabelValue_.value).in(labelValues));
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), userGuid));

				return p;
			}
		};
	}
}
