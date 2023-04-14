package lithium.service.avatar.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.data.entities.Avatar_;
import lithium.service.avatar.data.entities.Domain;
import lithium.service.avatar.data.entities.Domain_;

public class AvatarSpecifications {
	public static Specification<Avatar> any(String search) {
		return new Specification<Avatar>() {
			@Override
			public Predicate toPredicate(Root<Avatar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.like(cb.upper(root.get(Avatar_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Avatar_.description)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Avatar> domain(String domain) {
		return new Specification<Avatar>() {
			@Override
			public Predicate toPredicate(Root<Avatar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Avatar, Domain> domainJoin = root.join(Avatar_.domain, JoinType.INNER);
				Predicate p = cb.equal(cb.upper(domainJoin.get(Domain_.name)), domain.toUpperCase());
				return p;
			}
		};
	}
}
