package lithium.service.accounting.domain.v2.storage.specifications;

import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.AccountCode_;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.Currency_;
import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.entities.Domain_;
import lithium.service.accounting.domain.v2.storage.entities.Label;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue_;
import lithium.service.accounting.domain.v2.storage.entities.Label_;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.Period_;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomainLabelValue_;
import lithium.service.accounting.domain.v2.storage.entities.TransactionType;
import lithium.service.accounting.domain.v2.storage.entities.TransactionType_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class SummaryDomainLabelValueSpecifications {
	public static Specification<SummaryDomainLabelValue> find(final String domainName, final String currencyCode,
			final String accountCode, final String transactionTypeCode, final String labelName, final String labelValue,
			final int granularity, final Boolean testUsers) {
		return find(domainName, currencyCode, accountCode, transactionTypeCode, labelName, labelValue, granularity,
				null, null, testUsers);
	}

	public static Specification<SummaryDomainLabelValue> find(final String domainName, final String currencyCode,
			final String accountCode, final String transactionTypeCode, final String labelName, final String labelValue,
			final int granularity, final Date dateStart, final Date dateEnd, Boolean testUsers) {
		return new Specification<SummaryDomainLabelValue>() {
			@Override
			public Predicate toPredicate(Root<SummaryDomainLabelValue> root, CriteriaQuery<?> query,
			        CriteriaBuilder cb) {
				Join<SummaryDomainLabelValue, LabelValue> joinLabelValue = root.join(
						SummaryDomainLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);
				Join<SummaryDomainLabelValue, Period> joinPeriod = root.join(SummaryDomainLabelValue_.period,
						JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryDomainLabelValue, Currency> joinCurrency = root.join(SummaryDomainLabelValue_.currency,
						JoinType.INNER);
				Join<SummaryDomainLabelValue, AccountCode> joinAccountCode = root.join(
						SummaryDomainLabelValue_.accountCode, JoinType.INNER);
				Join<SummaryDomainLabelValue, TransactionType> joinTransactionType = root.join(
						SummaryDomainLabelValue_.transactionType, JoinType.INNER);


				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
				p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				if (testUsers != null)
				p = cb.and(p, cb.equal(root.get(SummaryDomainLabelValue_.testUsers),testUsers));

				if (dateStart != null) p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart),
						dateStart));
				if (dateEnd != null) p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));

				return p;
			}
		};
	}
}