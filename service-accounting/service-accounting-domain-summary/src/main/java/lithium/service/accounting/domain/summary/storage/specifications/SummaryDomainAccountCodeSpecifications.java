package lithium.service.accounting.domain.summary.storage.specifications;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.AccountCode_;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Currency_;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Domain_;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.Period_;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomain;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class SummaryDomainAccountCodeSpecifications {
	public static Specification<SummaryDomain> find(final String domainName, final String currencyCode,
			final String accountCode, final int granularity, final Date dateStart, final Date dateEnd) {
		return new Specification<SummaryDomain>() {
			@Override
			public Predicate toPredicate(Root<SummaryDomain> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				String[] accountCodes = accountCode.split(",");

				Join<SummaryDomain, Period> joinPeriod = root.join(SummaryDomain_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryDomain, Currency> joinCurrency = root.join(SummaryDomain_.currency, JoinType.INNER);
				Join<SummaryDomain, AccountCode> joinAccountCode = root.join(SummaryDomain_.accountCode, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, joinAccountCode.get(AccountCode_.code).in(accountCodes));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));
				
				return p;
			}
		};
	}
}