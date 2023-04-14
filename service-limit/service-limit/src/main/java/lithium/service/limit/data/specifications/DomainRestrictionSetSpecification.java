package lithium.service.limit.data.specifications;

import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.DomainRestrictionSet_;
import lithium.service.limit.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DomainRestrictionSetSpecification {
	public static Specification<DomainRestrictionSet> any(final String search) {
		return new Specification<DomainRestrictionSet>() {
			@Override
			public Predicate toPredicate(Root<DomainRestrictionSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if(search.isEmpty()) return null;
				Predicate p = cb.like(root.get(DomainRestrictionSet_.name), search + "%");
				return p;
			}
		};
	}

	public static Specification<DomainRestrictionSet> domains(final String[] domains) {
		return new Specification<DomainRestrictionSet>() {
			@Override
			public Predicate toPredicate(Root<DomainRestrictionSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<DomainRestrictionSet, Domain> domainJoin = root.join(DomainRestrictionSet_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}

	public static Specification<DomainRestrictionSet> deleted(final boolean deleted) {
		return new Specification<DomainRestrictionSet>() {
			@Override
			public Predicate toPredicate(Root<DomainRestrictionSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(DomainRestrictionSet_.deleted), deleted);
				return p;
			}
		};
	}

	public static Specification<DomainRestrictionSet> enabled(final boolean enabled) {
		return new Specification<DomainRestrictionSet>() {
			@Override
			public Predicate toPredicate(Root<DomainRestrictionSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(DomainRestrictionSet_.enabled), enabled);
				return p;
			}
		};
	}
}
