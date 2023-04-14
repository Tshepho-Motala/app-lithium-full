package lithium.service.access.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.access.data.entities.Domain;
import lithium.service.access.data.entities.Domain_;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.ListType;
import lithium.service.access.data.entities.ListType_;
import lithium.service.access.data.entities.List_;

public class ListSpecification {
	public static Specification<List> anyContains(final String value) {
		return new Specification<List>() {
			@Override
			public Predicate toPredicate(Root<List> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(List_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(List_.enabled).as(String.class)), "%" + value.toUpperCase() + "%"));
				Join<List, Domain> joinDomain = root.join(List_.domain, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinDomain.get(Domain_.name)), "%" + value.toUpperCase() + "%"));
				Join<List, ListType> joinType = root.join(List_.listType, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinType.get(ListType_.displayName)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<List> domainIn(java.util.List<String> domains) {
		return new Specification<List>() {
			@Override
			public Predicate toPredicate(Root<List> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<List, Domain> joinDomain = root.join(List_.domain, JoinType.INNER);
				Predicate p = joinDomain.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
}