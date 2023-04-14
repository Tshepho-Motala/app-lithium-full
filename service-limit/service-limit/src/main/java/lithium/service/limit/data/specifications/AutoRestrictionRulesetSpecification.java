package lithium.service.limit.data.specifications;

import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet_;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class AutoRestrictionRulesetSpecification {
	public static Specification<AutoRestrictionRuleSet> any(final String search) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return null;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> domains(final String[] domains) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<AutoRestrictionRuleSet, Domain> domainJoin = root.join(AutoRestrictionRuleSet_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> enabled(final boolean enabled) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(AutoRestrictionRuleSet_.enabled), enabled);
				return p;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> deleted(final boolean deleted) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(AutoRestrictionRuleSet_.deleted), deleted);
				return p;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> nameStartsWith(final String name) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(AutoRestrictionRuleSet_.name), name + "%");
				return p;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> lastUpdatedStart(final Date lastUpdatedStart) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(AutoRestrictionRuleSet_.lastUpdated).as(Date.class), lastUpdatedStart);
				return p;
			}
		};
	}

	public static Specification<AutoRestrictionRuleSet> lastUpdatedEnd(final Date lastUpdatedEnd) {
		return new Specification<AutoRestrictionRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoRestrictionRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(AutoRestrictionRuleSet_.lastUpdated).as(Date.class), lastUpdatedEnd);
				return p;
			}
		};
	}
}
