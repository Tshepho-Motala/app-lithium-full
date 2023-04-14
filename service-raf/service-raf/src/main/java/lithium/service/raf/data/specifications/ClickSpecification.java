package lithium.service.raf.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.raf.data.entities.Click;
import lithium.service.raf.data.entities.Click_;
import lithium.service.raf.data.entities.Domain;
import lithium.service.raf.data.entities.Domain_;
import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.entities.Referrer_;

public class ClickSpecification {
	public static Specification<Click> any(String search) {
		return new Specification<Click>() {
			@Override
			public Predicate toPredicate(Root<Click> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Click, Domain> domainJoin = root.join(Click_.domain, JoinType.INNER);
				Join<Click, Referrer> referrerJoin = root.join(Click_.referrer, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(referrerJoin.get(Referrer_.playerGuid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Click_.timestamp).as(String.class)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Click_.ip)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Click_.userAgent)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Click> domain(String domainName) {
		return new Specification<Click>() {
			@Override
			public Predicate toPredicate(Root<Click> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Click, Domain> domainJoin = root.join(Click_.domain, JoinType.INNER);
				Predicate p = cb.equal(cb.upper(domainJoin.get(Domain_.name)), domainName.toUpperCase());
				return p;
			}
		};
	}
	
	public static Specification<Click> referrerIs(String referrer) {
		return new Specification<Click>() {
			@Override
			public Predicate toPredicate(Root<Click> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Click, Referrer> referrerJoin = root.join(Click_.referrer, JoinType.INNER);
				Predicate p = cb.like(cb.upper(referrerJoin.get(Referrer_.playerGuid)), "%" + referrer.toUpperCase() + "%");
				return p;
			}
		};
	}
}
