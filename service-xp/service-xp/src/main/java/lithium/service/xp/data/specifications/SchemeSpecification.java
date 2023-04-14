package lithium.service.xp.data.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.xp.data.entities.Domain;
import lithium.service.xp.data.entities.Domain_;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.data.entities.Scheme_;
import lithium.service.xp.data.entities.Status;
import lithium.service.xp.data.entities.Status_;

public class SchemeSpecification {
	public static Specification<Scheme> any(String search) {
		return new Specification<Scheme>() {
			@Override
			public Predicate toPredicate(Root<Scheme> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Scheme, Domain> domainJoin = root.join(Scheme_.domain, JoinType.INNER);
				Join<Scheme, Status> statusJoin = root.join(Scheme_.status, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(statusJoin.get(Status_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Scheme_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Scheme_.description)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Scheme> domains(List<String> domains) {
		return new Specification<Scheme>() {
			@Override
			public Predicate toPredicate(Root<Scheme> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Scheme, Domain> domainJoin = root.join(Scheme_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
}
