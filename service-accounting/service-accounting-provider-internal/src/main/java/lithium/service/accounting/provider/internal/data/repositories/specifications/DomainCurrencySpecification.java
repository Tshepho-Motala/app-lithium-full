package lithium.service.accounting.provider.internal.data.repositories.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Currency_;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency_;
import lithium.service.accounting.provider.internal.data.entities.Domain_;

public class DomainCurrencySpecification {
	public static Specification<DomainCurrency> any(String search) {
		return new Specification<DomainCurrency>() {
			@Override
			public Predicate toPredicate(Root<DomainCurrency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<DomainCurrency, Currency> currencyJoin = root.join(DomainCurrency_.currency, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(currencyJoin.get(Currency_.code)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(currencyJoin.get(Currency_.name)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<DomainCurrency> domain(String domainName) {
		return new Specification<DomainCurrency>() {
			@Override
			public Predicate toPredicate(Root<DomainCurrency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<DomainCurrency, Domain> domainJoin = root.join(DomainCurrency_.domain, JoinType.INNER);
				Predicate p = cb.equal(cb.upper(domainJoin.get(Domain_.name)), domainName.toUpperCase());
				return p;
			}
		};
	}
}
