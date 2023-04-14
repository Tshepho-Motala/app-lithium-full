package lithium.service.accounting.provider.internal.data.repositories.specifications;

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
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry_;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue_;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType_;
import lithium.service.accounting.provider.internal.data.entities.Transaction_;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Date;
import java.util.List;

public class TransactionEntrySpecifications {
	public static Specification<TransactionEntry> find(
		final String transactionTypeCode,
		final String accountCode,
		final String ownerGuid,
		final String domainName,
		final String currencyCode,
		final String labelValue,
		final String labelName

	) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Account> joinAccount = root.join(TransactionEntry_.account, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);
				Join<Account, Domain> joinDomain = joinAccount.join(Account_.domain, JoinType.INNER);
				
				Root<Transaction> tranRoot = query.from(Transaction.class);
				Join<Transaction, TransactionType> joinTransactionType = tranRoot.join(Transaction_.transactionType, JoinType.INNER);
				
				Root<TransactionLabelValue> tranLvRoot = query.from(TransactionLabelValue.class);
				Join<TransactionLabelValue, LabelValue> joinLabelValue = tranLvRoot.join(TransactionLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);
				
				Predicate p = cb.equal(tranLvRoot.get(TransactionLabelValue_.transactionId), tranRoot.get(Transaction_.id));
				p = cb.and(p, cb.equal(joinDomain.get(Domain_.name), domainName));
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), ownerGuid));
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> any(final String search) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Transaction> transactionJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
				return cb.or(
					cb.like(transactionJoin.get(Transaction_.id).as(String.class), search+ "%")
				);
			}
		};
	}

	public static Specification<TransactionEntry> likeWithLabelValue(final String labelName, final String labelValue) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Transaction> tranJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
				Root<TransactionLabelValue> tranLvRoot = query.from(TransactionLabelValue.class);
				Join<TransactionLabelValue, LabelValue> labelValueJoin = tranLvRoot.join(TransactionLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> labelJoin = labelValueJoin.join(LabelValue_.label, JoinType.INNER);

				Predicate p = cb.equal(tranLvRoot.get(TransactionLabelValue_.transactionId), tranJoin.get(Transaction_.id));
				p = cb.and(p, cb.equal(labelJoin.get(Label_.name), labelName));
				p = cb.and(p, cb.like(labelValueJoin.get(LabelValue_.value), labelValue + "%"));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> equalWithLabelValue(final String labelName, final String labelValue) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Transaction> tranJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
				Root<TransactionLabelValue> tranLvRoot = query.from(TransactionLabelValue.class);
				Join<TransactionLabelValue, LabelValue> labelValueJoin = tranLvRoot.join(TransactionLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> labelJoin = labelValueJoin.join(LabelValue_.label, JoinType.INNER);

				Predicate p = cb.equal(tranLvRoot.get(TransactionLabelValue_.transactionId), tranJoin.get(Transaction_.id));
				p = cb.and(p, cb.equal(labelJoin.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(labelValueJoin.get(LabelValue_.value), labelValue));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> notEqualForFieldName(final SingularAttribute<TransactionEntry, Long> fieldName, final Long fieldValue) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p =  cb.notEqual(root.get(fieldName), fieldValue);
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> dateRangeStart(final Date dateRangeStart) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(TransactionEntry_.date).as(Date.class), new java.sql.Date(dateRangeStart.getTime()));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> dateRangeEnd(final Date dateRangeEnd) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(TransactionEntry_.date).as(Date.class), new java.sql.Date(dateRangeEnd.getTime()));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> transactionIdStartsWith(final String transactionIdStartsWith) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Transaction> transactionJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
				Predicate p = cb.like(transactionJoin.get(Transaction_.id).as(String.class), transactionIdStartsWith + "%");
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> domains(final List<String> domains) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Account> accountJoin = root.join(TransactionEntry_.account, JoinType.INNER);
				Join<Account, Domain> domainJoin = accountJoin.join(Account_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> user(final String userGuid) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Account> accountJoin = root.join(TransactionEntry_.account, JoinType.INNER);
				Join<Account, User> ownerJoin = accountJoin.join(Account_.owner, JoinType.INNER);
				Predicate p = cb.equal(ownerJoin.get(User_.guid), userGuid);
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> transactionTypeCode(final List<String> transactionTypeCode) {
		return new Specification<TransactionEntry>() {
			@Override
			public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TransactionEntry, Transaction> accountJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
				Join<Transaction, TransactionType> ownerJoin = accountJoin.join(Transaction_.transactionType, JoinType.INNER);
				Predicate p = cb.conjunction();
				p = cb.and(p, ownerJoin.get(TransactionType_.code).in(transactionTypeCode));
				return p;
			}
		};
	}

	public static Specification<TransactionEntry> accountCode(final String[] code) {
		return (root, query, cb) -> {
			Join<TransactionEntry, Account> accountJoin = root.join(TransactionEntry_.account, JoinType.INNER);
			Join<Account, AccountCode> accountCodeJoin = accountJoin.join(Account_.accountCode, JoinType.INNER);
			return accountCodeJoin.get(AccountCode_.code).in(code);
		};
	}

    public static Specification<TransactionEntry> excludeTransactionTypeCode(List<String> excludedTransactionTypes) {
        return new Specification<TransactionEntry>() {
            @Override
            public Predicate toPredicate(Root<TransactionEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<TransactionEntry, Transaction> accountJoin = root.join(TransactionEntry_.transaction, JoinType.INNER);
                Join<Transaction, TransactionType> ownerJoin = accountJoin.join(Transaction_.transactionType, JoinType.INNER);
                Predicate p = cb.conjunction();
                p = cb.and(p, cb.not(ownerJoin.get(TransactionType_.code).in(excludedTransactionTypes)));
                return p;
            }
        };
    }
}
