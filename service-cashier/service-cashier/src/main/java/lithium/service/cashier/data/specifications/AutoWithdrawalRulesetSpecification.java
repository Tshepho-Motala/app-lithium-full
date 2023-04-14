package lithium.service.cashier.data.specifications;

import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet_;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class AutoWithdrawalRulesetSpecification {
	public static Specification<AutoWithdrawalRuleSet> any(final String search) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if(search.isEmpty()) {
					return null;
				}
				Predicate p = cb.like(root.get(AutoWithdrawalRuleSet_.name), search + "%");
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> domains(final String[] domains) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<AutoWithdrawalRuleSet, Domain> domainJoin = root.join(AutoWithdrawalRuleSet_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> enabled(final boolean enabled) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(AutoWithdrawalRuleSet_.enabled), enabled);
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> deleted(final boolean deleted) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(AutoWithdrawalRuleSet_.deleted), deleted);
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> nameStartsWith(final String name) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(AutoWithdrawalRuleSet_.name), name + "%");
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> lastUpdatedStart(final Date lastUpdatedStart) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(AutoWithdrawalRuleSet_.lastUpdated).as(Date.class), lastUpdatedStart);
				return p;
			}
		};
	}

	public static Specification<AutoWithdrawalRuleSet> lastUpdatedEnd(final Date lastUpdatedEnd) {
		return new Specification<AutoWithdrawalRuleSet>() {
			@Override
			public Predicate toPredicate(Root<AutoWithdrawalRuleSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(AutoWithdrawalRuleSet_.lastUpdated).as(Date.class), lastUpdatedEnd);
				return p;
			}
		};
	}
}
