package lithium.service.accounting.provider.internal.data.repositories.specifications;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountCode_;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Currency_;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Domain_;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.Period_;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType_;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class SummaryDomainTransactionTypeSpecifications {

	public static Specification<SummaryDomainTransactionType> find(
		final String domainName,
		final String currencyCode,
		final String accountCode, 
		final String transactionTypeCode,
		final int granularity,
		final Date dateStart,
		final Date dateEnd
	) {
		return new Specification<SummaryDomainTransactionType>() {
			@Override
			public Predicate toPredicate(Root<SummaryDomainTransactionType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				String[] accountCodes = accountCode.split(",");

				Join<SummaryDomainTransactionType, Period> joinPeriod = root.join(SummaryDomainTransactionType_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryDomainTransactionType, Currency> joinCurrency = root.join(SummaryDomainTransactionType_.currency, JoinType.INNER);
				Join<SummaryDomainTransactionType, AccountCode> joinAccountCode = root.join(SummaryDomainTransactionType_.accountCode, JoinType.INNER);
				Join<SummaryDomainTransactionType, TransactionType> joinTransactionType = root.join(SummaryDomainTransactionType_.transactionType, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
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
}