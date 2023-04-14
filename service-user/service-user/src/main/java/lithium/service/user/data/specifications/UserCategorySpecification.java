package lithium.service.user.data.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserCategory_;

public class UserCategorySpecification {

	public static Specification<UserCategory> any(final String search) {
		return new Specification<UserCategory>() {
			@Override
			public Predicate toPredicate(Root<UserCategory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.like(cb.upper(root.get(UserCategory_.description)), search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(UserCategory_.name)), search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<UserCategory> domainIn(final List<Domain> domains) {
		return new Specification<UserCategory>() {
			@Override
			public Predicate toPredicate(Root<UserCategory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<UserCategory, Domain> joinDomain = root.join(UserCategory_.domain, JoinType.INNER);
				Predicate p = joinDomain.in(domains); 
				return p;
			}
		};
	}
}
